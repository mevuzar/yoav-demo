package com.hamlazot.scripts.interpreters.cqrs

import akka.actor.Props
import akka.cluster.sharding.ShardRegion
import com.hamlazot.domain.impl.model.AccountModel.UserAccount

import scala.concurrent.duration._
import akka.persistence.PersistentView

/**
 * @author yoav @since 9/11/16.
 */
class AccountView extends PersistentView with Passivation with Logging with UnknownCommandSupport{

 import CommandAndQueryProtocol._

   override def viewId: String = self.path.parent.name + "-" + self.path.name

   override val persistenceId: String = "AccountWriter" + "-" + self.path.name

   context.setReceiveTimeout(1 minute)

   override def receive: Receive = passivate(initial).orElse(unknownCommand)


   def initial: Receive = {

     case StoreAccountEvent(account, token) =>
       val state = AccountState(account)
       context.become(passivate(accountExists(state).orElse(unknownCommand)))
     case SearchAccountById(id) =>
       sender ! AccountDoesNotExist(id)

   }

  def accountExists(state: AccountState): Receive = {
    case SearchAccountById(id) =>
      sender ! state
  }

 }

object AccountView {
  val shardName = "AccountView"

  def props():Props = Props(classOf[AccountView])

  //val idExtractor = IdExtractor
}


case class AccountState(userAccount: UserAccount)