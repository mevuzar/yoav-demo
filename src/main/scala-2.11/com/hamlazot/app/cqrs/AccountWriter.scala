package com.hamlazot.app.cqrs

import java.util.UUID

import akka.actor.{ActorRef, PoisonPill, Props}
import akka.cluster.sharding.ShardRegion
import akka.cluster.sharding.ShardRegion.Passivate
import akka.pattern.ask
import akka.persistence.{Update, DeleteMessagesSuccess, PersistentActor}
import akka.util.Timeout
import com.hamlazot.DataDSL.{DataOpteration, DataStoreRequest}
import com.hamlazot.app.cqrs.AccountView.{AccountEnvelope, GetAccountQuery}
import com.hamlazot.app.cqrs.AccountWriter.{AccountAck, AccountCreatedEvent, AccountCreationAck, AccountDeletedEvent, AccountDeletionAck, AccountEvent, AccountMailChangedEvent, AccountMailUpdateAck, AccountTokenRefreshAck, AccountTokenRefreshedEvent, ChangeAccountMail, CreateAccount, RefreshToken}
import com.hamlazot.domain.impl.model.AccountModel.{AccountCredentials, UserAccount}
import com.hamlazot.domain.impl.server.accounts.dal.AccountRepositoryF.DSL.{AccountQuery, DeleteAccount, StoreAccount, UpdateMail}
import com.hamlazot.scripts.interpreters.cqrs.{Logging, Passivation, UnknownCommandSupport}

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
          val futureEnvelope = (reader ? GetAccountQuery(id)).mapTo[AccountEnvelope]
          val eventualAccount = futureEnvelope.map(env => env.userAccount)

          Right (eventualAccount)


  }
}

}

class AccountWriter(view: ActorRef)
  extends PersistentActor
  with UnknownCommandSupport
  with Passivation
  with Logging {

  override def persistenceId: String = self.path.parent.name + "-" + self.path.name

  context.setReceiveTimeout(2.minutes)

  private final case class ProcessedCommand(event: Option[AccountEvent], ack: AccountAck, newReceive: Option[Receive])

  def handleProcessedCommand(sendr: ActorRef, processedCommand: ProcessedCommand): Unit = {

    processedCommand.event.fold(sender() ! processedCommand.ack) { evt =>
      persist(evt.logDebug("+++++++++++++  evt = " + _.toString)) { persistedEvt =>
        view ! Update(await = true)
        processedCommand.event.map(evt => {
          view ! evt
        })
        sendr ! processedCommand.ack
        processedCommand.newReceive.fold({})(context.become)
      }
    }
  }

  private var accountRecoverStateMaybe: Option[UserAccount] = None

  override def receiveRecover: Receive = {
    case evt: AccountCreatedEvent =>
      accountRecoverStateMaybe = Some(UserAccount.tupled(AccountCreatedEvent.unapply(evt).get))

    case evt: AccountEvent =>
      accountRecoverStateMaybe.map(state =>
        updateState(state, evt.logDebug("receiveRecover" + _.toString)))
  }

  override def receiveCommand: Receive = passivate(initialState).orElse(unknownCommand)

  def initialState: Receive = {
    case CreateAccount(id, credentials, token, name, mail) =>
      handleProcessedCommand(sender(),
        ProcessedCommand(Some(AccountCreatedEvent(id, credentials, token, name, mail)), AccountCreationAck(id), Some(createdAccountState)))
  }

  def createdAccountState: Receive = {
    case RefreshToken(id, token) =>
      handleProcessedCommand(sender(), ProcessedCommand(Some(AccountTokenRefreshedEvent(id, token)), AccountTokenRefreshAck(id), None))


    case ChangeAccountMail(id, mail) =>
      handleProcessedCommand(sender(), ProcessedCommand(Some(AccountMailChangedEvent(id, mail)), AccountMailUpdateAck(id), None))

    case DeleteAccount(id) => {
      deleteMessages(0L)
      view ! AccountDeletedEvent(id)
      sender() ! AccountDeletionAck(id)
    }

    case DeleteMessagesSuccess(seqNr) => {
      context.parent ! Passivate(PoisonPill)
    }

    case PoisonPill => context.stop(self)

  }

  def updateState(state: UserAccount, evt: AccountEvent): UserAccount = {
    evt match {
      case tokenRef: AccountTokenRefreshedEvent => state.copy(token = tokenRef.token)
      case mailChange: AccountMailChangedEvent => state.copy(mail = mailChange.mail)
      case _ => state
    }
  }
}

object AccountWriter {

  def props(readRegion: ActorRef): Props = Props(new AccountWriter(readRegion))

  val idExtractor: ShardRegion.ExtractEntityId = {
    case cmd: AccountCommand => (cmd.id.toString, cmd)
  }

  val shardResolver: ShardRegion.ExtractShardId = {
    case cmd: AccountCommand => (math.abs(cmd.id.hashCode) % 100).toString
  }

  val shardName: String = "Account"


  sealed trait AccountMessage {
    val id: UUID
  }

  sealed trait AccountCommand extends AccountMessage

  case class CreateAccount(id: UUID,
                           credentials: AccountCredentials,
                           token: UUID,
                           name: String,
                           mail: String) extends AccountCommand

  case class RefreshToken(id: UUID, token: UUID) extends AccountCommand

  case class ChangeAccountMail(id: UUID, mail: String) extends AccountCommand

  case class DeleteAccount(id: UUID) extends AccountCommand


  sealed trait AccountEvent

  case class AccountCreatedEvent(id: UUID,
                                 credentials: AccountCredentials,
                                 token: UUID,
                                 name: String,
                                 mail: String) extends AccountEvent

  case class AccountTokenRefreshedEvent(id: UUID, token: UUID) extends AccountEvent

  case class AccountMailChangedEvent(id: UUID, mail: String) extends AccountEvent

  case class AccountDeletedEvent(id: UUID) extends AccountEvent


  sealed trait AccountAck extends AccountMessage

  case class AccountCreationAck(id: UUID) extends AccountAck

  case class AccountDeletionAck(id: UUID) extends AccountAck

  case class AccountTokenRefreshAck(id: UUID) extends AccountAck

  case class AccountMailUpdateAck(id: UUID) extends AccountAck


  private case class State(account: UserAccount, exists: Boolean) {
    def updated(evt: AccountEvent): State = evt match {
      case AccountCreatedEvent(id, credentials, token, name, mail) => copy(account.copy(id, credentials, token, name, mail), true)
      case AccountTokenRefreshedEvent(id, token) => copy(account.copy(token = token))
      case AccountMailChangedEvent(id, mail) => copy(account.copy(mail = mail))
    }
  }

}
