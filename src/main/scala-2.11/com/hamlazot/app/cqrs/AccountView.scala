package com.hamlazot
package app.cqrs

import java.util.UUID

import akka.actor.Actor.Receive
import akka.actor.{Props, PoisonPill}
import akka.cluster.sharding.ShardRegion
import akka.cluster.sharding.ShardRegion.Passivate
import akka.persistence.PersistentView
import com.hamlazot.app.cqrs.AccountView.{AccountEnvelope, GetAccountQuery}
import com.hamlazot.app.cqrs.AccountWriter.{AccountDeletedEvent, AccountMailChangedEvent, AccountTokenRefreshedEvent, AccountCreatedEvent}
import com.hamlazot.scripts.interpreters.cqrs.{UnknownCommandSupport, Passivation}
import com.hamlazot.domain.impl.model.AccountModel.{AccountCredentials, UserAccount}
import scala.concurrent.duration._

/**
 * @author yoav @since 9/12/16.
 */
class AccountView extends PersistentView with Passivation with UnknownCommandSupport{
  override def viewId: String = self.path.parent.name + "-" + self.path.name

  override def persistenceId: String = "Account" + "-" + self.path.name

  context.setReceiveTimeout(1 minute)

  override def receive: Receive = passivate(initial).orElse(unknownCommand)

  def initial: Receive = {
    case AccountCreatedEvent(id, credentials, token, name, mail) =>

      val newState = AccountEnvelope(id, UserAccount(id, credentials, token, name, mail))
      context.become(passivate(createdState(newState)).orElse(unknownCommand))

    case GetAccountQuery(id) =>
      sender() ! AccountEnvelope(UUID.randomUUID, UserAccount(UUID.randomUUID, AccountCredentials("yoyo", "1234"), UUID.randomUUID, "yoyo", "yoav.sadeh@gmail.com"))
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
    case get: GetAccountQuery => (get.id.toString, get)
  }

  val shardResolver: ShardRegion.ExtractShardId = msg => msg match {
    case get: GetAccountQuery    => (math.abs(get.id.hashCode) % 100).toString
  }


  sealed trait AccountQuery{
    val id: UUID
  }
  case class GetAccountQuery(id: UUID) extends AccountQuery

  case class AccountEnvelope(id: UUID, userAccount: UserAccount)
}
