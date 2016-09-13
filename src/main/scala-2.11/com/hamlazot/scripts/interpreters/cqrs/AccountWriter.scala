package com.hamlazot.scripts.interpreters.cqrs

import akka.actor.ActorRef
import akka.persistence.{PersistentActor, Update}
import com.hamlazot.domain.impl.model.AccountModel.UserAccount

import scala.concurrent.duration._

/**
 * @author yoav @since 9/11/16.
 */
class AccountWriter(reader: ActorRef) extends PersistentActor with Passivation with Logging with UnknownCommandSupport {


  import CommandAndQueryProtocol._

  override def persistenceId: String = self.path.parent.name + "-" + self.path.name

  context.setReceiveTimeout(1 minute)

  /** Used only for recovery */
  private var accountRecoverStateMaybe: Option[UserAccount] = None


  override def receiveRecover: Receive = {
    case a@StoreAccountEvent(userAccount, token) =>
      accountRecoverStateMaybe = Some(userAccount)
  }

  override def receiveCommand: Receive = passivate(storeReceive).orElse(unknownCommand)

  def storeReceive: Receive = {
    case a@StoreAccountEvent(userAccount, token) => {
      persist(a) { evt => println(s"event $evt persisted") }
      reader ! Update(await = true)
      sender ! AccountPersisted(userAccount.id)
    }
  }
}
