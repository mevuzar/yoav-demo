package com.hamlazot
package product_scripts


import com.typesafe.scalalogging.LazyLogging
import implementation.interpreters.StdInInteractionInterpreter
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/**
 * @author yoav @since 9/9/16.
 */
object UberScript {

  def signUpFlow: Unit = {

    SignUpScript.userInputToSignUpRequest(StdInInteractionInterpreter) onComplete {
      case Success(account) =>
        println(s"Here you go:\n$account")
      case Failure(e) =>
        println(s"Guess what you dush: ${e.getMessage}\ntry again...")
        signUpFlow
    }
  }

  def updateMailFlow: Unit = {
    println("Update yer mail...")
    UpdateMailScript.updateMail(StdInInteractionInterpreter) onComplete {
      case Success(account) =>
        println(s"Here you go:\n$account")
      case Failure(e) =>
        println(s"Guess what you dush: ${e.getMessage}\ntry again...")
        updateMailFlow
    }
  }

}

object ScriptBoot extends App with LazyLogging{
  logger.info("Start of program")
  UberScript.signUpFlow
  Thread.sleep(1000)
  UberScript.updateMailFlow
}
