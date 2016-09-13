package com.hamlazot.implementation.cqrs

import akka.actor.Actor

/**
 * @author yoav @since 9/11/16.
 */
trait UnknownCommandSupport extends Logging {
  this: Actor =>
  def unknownCommand: Receive = {
    case other => {
      other.logDebug("unknownCommand: " + _.toString)
      sender() ! InvalidCommand(other)
    }
  }
}

case class InvalidCommand(cmd: Any)
