package com.hamlazot
package scripts.interpreters

import java.time.{ZoneId, ZonedDateTime}


import domain.contract.client.{Interact, Question, PreDefInteractions}
import PreDefInteractions._
import scala.annotation.tailrec
import scala.io.StdIn._
import scala.util.{Success, Failure}
import scalaz.{Id, ~>}

/**
 * @author yoav @since 7/16/16.
 */
object StdInInteractionInterpreter extends (Question ~> Id.Id){

  override def apply[A](fa: Question[A]): Id.Id[A] = fa match{
    case Interact(interaction) =>
      interaction match{
        case StringInteraction(q) => readLine(q + "\n")
        case DateInteraction(q) =>
          println(q + "\n")
          val year = readLine("year:\n").toInt
          val month = readLine("month:\n").toInt
          val day = readLine("day:\n").toInt
          val hours = readLine("hours:\n").toInt
          val minutes = readLine("minutes:\n").toInt
          val seconds = readLine("seconds:\n").toInt
          val date = ZonedDateTime.of(year, month, day, hours, minutes, seconds, 0, ZoneId.systemDefault())
          date

        case IntInteraction(q) =>
          println(q + "\n")
          readInt

        case DoubleInteraction(q) =>
          println(q + "\n")
          readDouble

        case UUIDInteraction(q) => java.util.UUID.fromString(readLine(q + "\n"))

        case BooleanInteraction(q) =>
          @tailrec
          def getAnswer: Boolean ={
            val reply = readLine(q + "\n")
            if (Seq("true", "yes", "y", "yeah", "yap").contains(reply.toLowerCase)) true
            else if (Seq("false", "no", "n", "nah", "nope").contains(reply.toLowerCase)) false
            else {
              println(s"you gave an illegal answer $reply to the Boolean question: $q..." +
              s"\ntry again(possible answers are:[true, yes, y, yeah, yap]/[false, no, n, nah, nope])")
            getAnswer
            }
          }

          getAnswer

        case StringBasedTransformation(q, f) =>
          @tailrec
          def getAnswer: A = {
            val reply = readLine(q)
            f(reply) match {
              case Success(a) => a
              case Failure(e) =>
                println(s"There was a problem transforming $reply to ${f.getClass.getTypeParameters()}")
                getAnswer
            }
        }
          getAnswer
      }


//    case InteractComplex(interactions: Seq[Interaction[_]]) =>
//      interactions.map(i => apply(Interact(i)))
  }
}
