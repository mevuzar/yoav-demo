package com.hamlazot
package implementation.cqrs

import java.util.UUID

import akka.actor.{ActorLogging, PoisonPill, Props}
import akka.cluster.sharding.ShardRegion
import akka.cluster.sharding.ShardRegion.Passivate
import akka.persistence.PersistentView
import domain.impl.model.AccountModel._
import scala.concurrent.duration._
import AccountWriter._
import AccountView._

/**
 * @author yoav @since 9/12/16.
 */
class AccountView extends PersistentView with Passivation with UnknownCommandSupport with ActorLogging{
  override def viewId: String = self.path.parent.name + "-" + self.path.name

  override def persistenceId: String = "AccountWriter" + "-" + self.path.name

  context.setReceiveTimeout(1 minute)

  override def receive: Receive = passivate(initial).orElse(unknownCommand)

  def initial: Receive = {
    case AccountCreatedEvent(id, credentials, token, name, mail) =>

      val newState = AccountEnvelope(id, UserAccount(id, credentials, token, name, mail))
      context.become(passivate(createdState(newState)).orElse(unknownCommand))

    case GetAccountQuery(id) =>
      logger.warn(s"premature account request for id $id")
      sender() ! PrematureRequest(id)
  }

  def createdState(state: AccountEnvelope): Receive = {
    case AccountTokenRefreshedEvent(id, token) =>
      val newState = state.copy(userAccount = state.userAccount.copy(token = token))
      context.become(passivate(createdState(newState)).orElse(unknownCommand))

    case AccountMailChangedEvent(id, mail) =>
      val newState = state.copy(userAccount = state.userAccount.copy(mail = mail))
      context.become(passivate(createdState(newState)).orElse(unknownCommand))

    case AccountDeletedEvent(id) =>
      context.parent ! Passivate(PoisonPill)

    case GetAccountQuery(id) =>
      sender() ! state
  }

}

object AccountView {

  val shardName: String = "AccountView"

  def props() = Props(new AccountView)

  val idExtractor: ShardRegion.ExtractEntityId = {
    case cmd: AccountCommand => (cmd.id.toString, cmd)
  }

  val shardResolver: ShardRegion.ExtractShardId = msg => msg match {
    case cmd: AccountCommand => (math.abs(cmd.id.hashCode) % 100).toString
  }


  sealed trait AccountQuery {
    val id: UUID
  }


  case class AccountEnvelope(id: UUID, userAccount: UserAccount)

  case class PrematureRequest(id: UUID)

}
