package com.hamlazot.implementation.cqrs

import java.util.UUID

import akka.actor.ActorRef
import akka.pattern.ask
import akka.persistence.Update
import akka.util.Timeout
import com.hamlazot.DataDSL.{DataOpteration, DataStoreRequest}
import com.hamlazot.domain.impl.model.AccountModel.{UserAccount, AccountCredentials, UserAccountFactory}
import com.hamlazot.domain.impl.server.accounts.dal.AccountRepositoryF.DSL.{AccountQuery, DeleteAccount, StoreAccount, UpdateMail}
import com.hamlazot.implementation.cqrs.AccountView.{PrematureRequest, AccountEnvelope}
import com.hamlazot.implementation.cqrs.AccountWriter.{GetAccountQuery, AccountCreationAck, AccountDeletionAck, AccountMailUpdateAck, ChangeAccountMail, CreateAccount}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Right
import scalaz.{Id, ~>}

/**
 * @author yoav @since 9/12/16.
 */

class AccountsRepositoryCQRSInterpreter(writer: ActorRef, reader: ActorRef)(implicit ctxt: ExecutionContext) extends (DataStoreRequest ~> Id.Id) {

  reader ! Update(await = true)

  override def apply[A](fa: DataStoreRequest[A]): Id.Id[A] = fa match {
    //case Pure(a) => a
    case DataOpteration(operation) =>
      operation match {

        case StoreAccount(account) =>
          implicit val timeout = Timeout(5 seconds)
          val token = UUID.randomUUID
          val response = (writer ? CreateAccount(account.id, account.credentials, token, account.name, account.mail)).mapTo[AccountCreationAck]
          val eventualUnit = response map (ack => (()))

          Right(eventualUnit)

        case DeleteAccount(userId) =>
          implicit val timeout = Timeout(3 seconds)
          val response = (writer ? DeleteAccount(userId)).mapTo[AccountDeletionAck]
          Right(response.map(ack => Future.successful(())))

        case UpdateMail(id, mail) =>
          implicit val timeout = Timeout(3 seconds)
          val response = (writer ? ChangeAccountMail(id, mail)).mapTo[AccountMailUpdateAck]

          val eventualAccount = response.map(ack => {
            val futureEnvelope = (reader ? GetAccountQuery(id)).mapTo[AccountEnvelope]
            futureEnvelope.map(env => env.userAccount)
          }).flatMap(identity)

          Right(eventualAccount)

        case AccountQuery(id) =>
          implicit val timeout = Timeout(5 seconds)
          val futureEnvelope = (reader ? GetAccountQuery(id))//.mapTo[AccountEnvelope]
          val eventualAccount = futureEnvelope.map {
            case env: AccountEnvelope => env.userAccount
            case PrematureRequest(id) => UserAccount(id, AccountCredentials("john", "doe"), UUID.randomUUID, "john", "john@doe.com")
          }
              Right(eventualAccount)


      }
  }

}