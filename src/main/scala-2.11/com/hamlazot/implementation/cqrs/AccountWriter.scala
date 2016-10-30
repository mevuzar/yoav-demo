package com.hamlazot.implementation.cqrs

import java.util.UUID

import akka.NotUsed
import akka.actor.{ActorRef, PoisonPill, Props}
import akka.cluster.sharding.ShardRegion
import akka.cluster.sharding.ShardRegion.Passivate
import akka.persistence.journal.leveldb.SharedLeveldbJournal
import akka.persistence.query.PersistenceQuery
import akka.persistence.query.journal.leveldb.scaladsl.LeveldbReadJournal
import akka.persistence.{DeleteMessagesSuccess, PersistentActor, Update}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import com.hamlazot.domain.contract.common.accounts.AccountRepositoryF
import com.hamlazot.domain.impl.common.accounts.AccountModel
import AccountModel.{AccountCredentials, UserAccount}
import AccountRepositoryF.DSL.DeleteAccount
import com.hamlazot.implementation.cqrs.AccountWriter.{AccountAck, AccountCreatedEvent, AccountCreationAck, AccountDeletedEvent, AccountDeletionAck, AccountEvent, AccountMailChangedEvent, AccountMailUpdateAck, AccountTokenRefreshAck, AccountTokenRefreshedEvent, ChangeAccountMail, CreateAccount, RefreshToken}

import scala.concurrent.duration._


class AccountWriter(view: ActorRef)
  extends PersistentActor
  with UnknownCommandSupport
  with Passivation
  with Logging {


  logger.info(s"got view: $view")
  val anotherView = view //context.actorOf(Props[AccountView])
  anotherView ! Update

  override def persistenceId: String = self.path.parent.name + "-" + self.path.name

  context.setReceiveTimeout(2.minutes)

  private final case class ProcessedCommand(event: Option[AccountEvent], ack: AccountAck, newReceive: Option[Receive])

  def handleProcessedCommand(sendr: ActorRef, processedCommand: ProcessedCommand): Unit = {

    processedCommand.event.fold(sender() ! processedCommand.ack) { evt =>
      persist(evt.logDebug("+++++++++++++  evt = " + _.toString)) { persistedEvt =>
        //anotherView ! Update//(await = true)
        processedCommand.event.map(evt => {
          //var vv = context.children.toList.find(_.path == anotherView.path)
          anotherView ! evt
          Thread.sleep(1000)
          //vv = context.children.toList.find(_.path == anotherView.path)
          anotherView ! evt
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

  case class GetAccountQuery(id: UUID) extends AccountCommand


  sealed trait AccountEvent extends AccountMessage

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
