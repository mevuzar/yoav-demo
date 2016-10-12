package com.hamlazot.implementation.interpreters

import java.util.UUID

import com.hamlazot.DataDSL.{DataOpteration, DataStoreRequest}
import com.hamlazot.domain.impl.common.accounts.{AccountModel, AccountRepositoryF}
import com.hamlazot.implementation.cqrs.AccountWriter.RefreshToken
import AccountModel.{UserAccount, UserToken}
import AccountRepositoryF.DSL.{UpdateMail, RefreshUserToken, AccountQueryex, DeleteAccount, StoreAccount}

import scala.collection.mutable.HashMap
import scala.concurrent.Future
import scala.util.{Failure, Right, Success, Try}
import scalaz.{Id, ~>}

/**
 * @author yoav @since 7/10/16.
 */
object AccountsRepositoryInMemInterpreter extends (DataStoreRequest ~> Id.Id) {

  import Id._

  val map = HashMap.empty[UUID, Tuple2[UserToken, UserAccount]]

  def apply[A](in: DataStoreRequest[A]): Id[A] =
    in match {
      //case Pure(a) => a
      case DataOpteration(operation) =>
        operation match {
          case AccountQueryex(userId) =>

            Right(Future.successful(map.find(a => a._1 == userId).get._2._2))

          case StoreAccount(account) =>
            Try {
              val token = UUID.randomUUID
              map.put(account.id, (UserToken(account.id, token), account))
            } match {
              case Success(some) => Right(Future.successful(()))

              case Failure(e) =>
                Right(Future.failed(e))
            }


          case RefreshUserToken(id) =>
            val token = UUID.randomUUID

            Try{
              val account = map(id)._2
              map.update(id, (UserToken(id,token), account))
            } match{
              case Success(s) => Right(Future.successful(token))
              case Failure(e) => Right(Future.failed(e))
            }

          case UpdateMail(id, mail) =>
            Try{
              val account = map(id)._2
              val token = map(id)._1
              map.update(id, (token, account.copy(mail = mail)))
            } match{
              case Success(s) => Right(Future.successful(map(id)._2))
              case Failure(e) => Right(Future.failed(e))
            }

          case DeleteAccount(userId) =>
            val fut = map.remove(userId).fold(Future.failed[Unit](new Exception)) { e => Future.successful(()) }
            Right(fut)

        }
    }
}




