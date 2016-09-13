package com.hamlazot.domain.contract

import java.time.ZonedDateTime

import scala.util.Try
import scalaz.{Coyoneda, Free}

/**
 * @author yoav @since 7/16/16.
 */
package object client {

  sealed trait Question[+A]

  type Answerable[A] = Coyoneda[Question, A]

  trait Interaction[+A]

  final case class Interact[+A](userInteraction: Interaction[A]) extends Question[A]

  //final case class InteractComplex[+A](userInteractions: Interaction[_]*) extends Question[A]

  object UserInteractions {
    def interact[A](userInteraction: Interaction[A]) = Free.liftFC(Interact(userInteraction))

    //def interact[A](userInteractions: Interaction[_]*) = Free.liftFC(InteractComplex(userInteractions: _*))
  }

  object PreDefInteractions{
    case class StringInteraction(question: String) extends Interaction[String]
    case class BooleanInteraction(question: String) extends Interaction[Boolean]
    case class IntInteraction(question: String) extends Interaction[Int]
    case class DoubleInteraction(question: String) extends Interaction[Double]
    case class DateInteraction(question: String) extends Interaction[ZonedDateTime]
    case class UUIDInteraction(question: String) extends Interaction[java.util.UUID]
    case class StringBasedTransformation[A](question: String, f: String => Try[A]) extends Interaction[A]
  }

}
