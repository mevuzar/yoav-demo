package com.hamlazot.scripts.interpreters.cqrs

import akka.actor.{PoisonPill, ReceiveTimeout, Actor}
import akka.actor.Actor.Receive
import akka.cluster.sharding.ShardRegion.Passivate

/**
 * @author yoav @since 9/11/16.
 */
trait Passivation extends Logging {
   this: Actor =>

   protected def passivate(receive: Receive): Receive = receive.orElse {
     // tell parent actor to send us a poisinpill
     case ReceiveTimeout =>
       self.logDebug("ReceiveTimeout: passivating. " + _.toString)
       context.parent ! Passivate(stopMessage = PoisonPill)

     // stop
     case PoisonPill => context.stop(self.logDebug("PoisonPill" + _.toString))
   }
 }
