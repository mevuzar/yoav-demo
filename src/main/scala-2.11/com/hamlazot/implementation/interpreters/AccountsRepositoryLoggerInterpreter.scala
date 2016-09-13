package com.hamlazot.implementation.interpreters

import java.util.UUID

import com.hamlazot.DataDSL.{DataOpteration, DataStoreRequest}
import com.hamlazot.FutureStringOr
import com.hamlazot.ServiceDSL.ServiceOperation
import com.hamlazot.domain.impl.model.AccountModel.{UserAccount, UserToken}
import com.hamlazot.domain.impl.server.accounts.dal.AccountRepositoryF.DSL.AccountDataCall

import scala.collection.mutable.HashMap
import scala.concurrent.Future
import scalaz.{Id, ~>}

/**
 * @author yoav @since 9/12/16.
 */
object AccountsRepositoryLoggerInterpreter extends (DataStoreRequest ~> Id.Id) {

  import Id._

  val map = HashMap.empty[UUID, (UserToken, UserAccount)]

  def apply[A](in: DataStoreRequest[A]): Id[A] =
    in match {

      case DataOpteration(operation) =>

        operation match {
          //          case AccountQuery(userId) =>
          //            Left(Future.successful(s"Querying account with id $userId"))
          //
          //          case StoreAccount(account) =>
          //            Left(Future.successful(s"Storing account $account"))
          //
          //          case DeleteAccount(userId) =>
          //            Left(Future.successful(s"Deleting account with id $userId"))
          case a: AccountDataCall[FutureStringOr[_]] => Left(Future.successful(s"$a"))
        }
    }


}
