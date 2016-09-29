package com.hamlazot
package implementation.cqrs

import java.util.UUID

import akka.NotUsed
import akka.actor.{ActorLogging, PoisonPill, Props}
import akka.cluster.sharding.ShardRegion
import akka.cluster.sharding.ShardRegion.Passivate
import akka.persistence.query.PersistenceQuery
import akka.persistence.query.journal.leveldb.scaladsl.LeveldbReadJournal
import akka.persistence.{PersistentActor, PersistentView}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import domain.impl.model.AccountModel._
import scala.concurrent.duration._
import AccountWriter._
import AccountView._

/**
 * @author yoav @since 9/12/16.
 */
class AccountViewie extends PersistentView with Passivation with UnknownCommandSupport with ActorLogging{
  override def viewId: String = self.path.parent.name + "-" + self.path.name

  override def persistenceId: String = "AccountWriter" + "-" + self.path.name

  context.setReceiveTimeout(2 minute)

  override def receive: Receive = passivate(initial).orElse(unknownCommand)
//  andThen {
//    case a => logger.debug(s"########################################\n" +
//      s"AccountView received message: $a\n" +
//      s"########################################\n")
//  }//passivate(initial).orElse(unknownCommand)

  def initial: Receive = {
    case AccountCreatedEvent(id, credentials, token, name, mail) =>
      logger.info(s"\n########################################\n" +
        s"received created event for id: $id\n" +
        s"##################################################")
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
      logger.info(s"\n########################################\n" +
        s"received mail update event for id: $id\n" +
        s"##################################################")
      val newState = state.copy(userAccount = state.userAccount.copy(mail = mail))

      context.become(passivate(createdState(newState)).orElse(unknownCommand))

    case AccountDeletedEvent(id) =>
      context.parent ! Passivate(PoisonPill)

    case GetAccountQuery(id) =>
      logger.info(s"\n########################################\n" +
        s"received get query for id: $id\n" +
        s"##################################################")
      sender() ! state
  }

}

object AccountViewie {

  val shardName: String = "AccountView"

  def props() =
    Props[AccountView]

  val idExtractor: ShardRegion.ExtractEntityId = {
    case cmd: AccountCommand =>
      (cmd.id.toString, cmd)
  }

  val shardResolver: ShardRegion.ExtractShardId = msg => msg match {
    case cmd: AccountCommand =>
      (math.abs(cmd.id.hashCode) % 100).toString
  }


  sealed trait AccountQuery {
    val id: UUID
  }


  case class AccountEnvelope(id: UUID, userAccount: UserAccount)

  case class PrematureRequest(id: UUID)

}

class AccountView extends PersistentActor with Passivation with UnknownCommandSupport with ActorLogging{

  logger.info(s"\n########################################\n" +
    s"AccountView actor created with path: ${self.path}\n" +
    s"##################################################")
  override def persistenceId: String = "AccountWriter" + "-" + self.path.name

  context.setReceiveTimeout(2 minute)

  override def receive: Receive = passivate(initial).orElse(unknownCommand)
  //  andThen {
  //    case a => logger.debug(s"########################################\n" +
  //      s"AccountView received message: $a\n" +
  //      s"########################################\n")
  //  }//passivate(initial).orElse(unknownCommand)

  def initial: Receive = {
    case AccountCreatedEvent(id, credentials, token, name, mail) =>
      logger.info(s"\n########################################\n" +
        s"received created event for id: $id\n" +
        s"##################################################")
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
      logger.info(s"\n########################################\n" +
        s"received mail update event for id: $id\n" +
        s"##################################################")
      val newState = state.copy(userAccount = state.userAccount.copy(mail = mail))

      context.become(passivate(createdState(newState)).orElse(unknownCommand))

    case AccountDeletedEvent(id) =>
      context.parent ! Passivate(PoisonPill)

    case GetAccountQuery(id) =>
      logger.info(s"\n########################################\n" +
        s"received get query for id: $id\n" +
        s"##################################################")
      sender() ! state
  }

  override def receiveRecover: Receive = passivate(initial).orElse(unknownCommand)

  override def receiveCommand: Receive = passivate(initial).orElse(unknownCommand)
}

object AccountView {

  val shardName: String = "AccountView"

  def props() = Props(new AccountView)

  val idExtractor: ShardRegion.ExtractEntityId = {
    case cmd: AccountMessage =>
      (cmd.id.toString, cmd)
  }

  val shardResolver: ShardRegion.ExtractShardId = msg => msg match {
    case cmd: AccountMessage =>
      (math.abs(cmd.id.hashCode) % 100).toString
  }


  sealed trait AccountQuery {
    val id: UUID
  }


  case class AccountEnvelope(id: UUID, userAccount: UserAccount)

  case class PrematureRequest(id: UUID)

}
