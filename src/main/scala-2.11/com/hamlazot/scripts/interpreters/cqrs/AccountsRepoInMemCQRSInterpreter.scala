package com.hamlazot.scripts.interpreters.cqrs

import java.util.UUID

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.util.Timeout
import com.hamlazot.DataDSL.{DataOpteration, DataStoreRequest}
import com.hamlazot.domain.impl.model.AccountModel.UserToken
import com.hamlazot.domain.impl.server.accounts.dal.AccountRepositoryF.DSL.{AccountQuery, StoreAccount}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.Right
import scalaz.{Id, ~>}

/**
 * @author yoav @since 9/10/16.
 */
class AccountsRepoInMemCQRSInterpreter(writer: ActorRef, reader: ActorRef)(implicit val ctxt: ExecutionContext) extends (DataStoreRequest ~> Id.Id) {

  import CommandAndQueryProtocol._

  implicit val timeout = Timeout(5 seconds)

  override def apply[A](fa: DataStoreRequest[A]): Id.Id[A] =
    fa match {
      //case Pure(a) => a
      case DataOpteration(operation) =>
        operation match {

          case StoreAccount(account) =>
            val eventualUnit = {
              val token = UUID.randomUUID
              val response = (writer ? StoreAccountEvent(account, UserToken(account.id, token))).mapTo[AccountPersisted]
              response
            } map (ack => ())

            Right(eventualUnit)

          case AccountQuery(id) =>
            val eventualAccount = {
              val response = (reader ? SearchAccountById(id)).mapTo[AccountState]
              response.map(state => state.userAccount)
            }

            Right(eventualAccount)
        }
    }
}

object AccountsRepoInMemCQRSInterpreter {

  def apply(implicit system: ActorSystem): AccountsRepoInMemCQRSInterpreter = {
    implicit val ctxt = system.dispatcher

    val reader = system.actorOf(Props[AccountView])
    val writer = system.actorOf(Props(classOf[AccountWriter], reader))
    new AccountsRepoInMemCQRSInterpreter(writer, reader)
  }
}












