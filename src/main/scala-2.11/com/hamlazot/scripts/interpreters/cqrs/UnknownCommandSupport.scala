package com.hamlazot.scripts.interpreters.cqrs

import akka.actor.Actor
import akka.actor.Actor.Receive
import CommandAndQueryProtocol.InvalidCommand

/**
 * @author yoav @since 9/11/16.
 */
trait UnknownCommandSupport extends Logging{ this: Actor =>
   def unknownCommand: Receive = {
     case other => {
       other.logDebug("unknownCommand: " + _.toString)
       sender() ! InvalidCommand(other)
     }
   }
 }
