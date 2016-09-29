package com.hamlatzot

/**
 * @author yoav @since 9/25/16.
 */

import akka.actor.Props
import akka.persistence.PersistentActor

object TestActor {
  def props(persistenceId: String): Props =
    Props(new TestActor(persistenceId))

  case class DeleteCmd(toSeqNr: Long = Long.MaxValue)

}

class TestActor(override val persistenceId: String) extends PersistentActor {

  import TestActor.DeleteCmd

  val receiveRecover: Receive = {
    case evt: String ⇒
  }

  val receiveCommand: Receive = {
    case DeleteCmd(toSeqNr) ⇒
      deleteMessages(toSeqNr)
      sender() ! s"$toSeqNr-deleted"

    case cmd: String ⇒
      persist(cmd) { evt ⇒
        sender() ! evt + "-done"
      }
  }

}
