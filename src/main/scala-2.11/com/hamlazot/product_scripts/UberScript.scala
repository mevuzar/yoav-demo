package com.hamlazot
package product_scripts


import java.util.UUID

import com.hamlazot.ServiceDSL.ServiceOperation
import com.hamlazot.app.AccountNodeBoot
import com.hamlazot.domain.impl.model.AccountModel.UserSignupDetails
import com.hamlazot.domain.impl.server.accounts.interpreter.AccountsServiceProduction
import com.hamlazot.implementation.cqrs.AccountsRepositoryCQRSInterpreter
import com.hamlazot.implementation.interpreters.{AccountsServiceProduct, ClosedCircuitClientAccount, DirectAccountsServiceCommunication, StdInInteractionInterpreter}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import scalaz.{Id, ~>}

/**
 * @author yoav @since 9/9/16.
 */
trait UberScript {

  val cccAccount: ClosedCircuitClientAccount

  object UpdateMailScript extends UpdateMailScript {
    override val closedCircuitClientAccount: ClosedCircuitClientAccount = cccAccount
  }

  object SignUpScript extends SignUpScript {
    override val closedCircuitClientAccount: ClosedCircuitClientAccount = cccAccount
  }

  def signUpFlow(details: UserSignupDetails): Unit = {

    cccAccount.signUp(details) onComplete {
      case Success(account) =>
        println(s"Here you go:\n$account")
      case Failure(e) =>
        println(s"Guess what you dush: ${e.getMessage}\nYou messed up!")

    }
  }

  def signUpFlowWithInteraction: Unit = {
    SignUpScript.userInputToSignUpRequest(StdInInteractionInterpreter) onComplete {
      case Success(account) =>
        println(s"Here you go:\n$account")
      case Failure(e) =>
        println(s"Guess what you dush: ${e.getMessage}\ntry again...")
        signUpFlowWithInteraction
    }
  }

  def updateMailFlowWithInteraction: Unit = {
    println("Update yer mail...")
    UpdateMailScript.updateMail(StdInInteractionInterpreter) onComplete {
      case Success(account) =>
        println(s"Here you go:\n$account")
      case Failure(e) =>
        println(s"Guess what you dush: ${e.getMessage}\ntry again...")
        updateMailFlowWithInteraction
    }
  }

  def updateMailFlow(mailUpdateRequest: (UUID, String)): Unit = {

    println("Update yer mail...")

    cccAccount.changeMailAddress(mailUpdateRequest) onComplete {
      case Success(account) =>
        println(s"Here you go:\n$account")
      case Failure(e) =>
        println(s"Guess what you dush: ${e.getMessage}\nYou messed up!")

    }
  }

}

object ScriptBoot extends App with UberScript with LazyLogging {
  logger.info("Start of program")


  override val cccAccount: ClosedCircuitClientAccount = new ClosedCircuitClientAccount {
    implicit private val accountRepo = getRepo
    implicit private val accountService = new AccountsServiceProduct(accountRepo)
    override val accountServiceCommunication: ~>[ServiceOperation, Id.Id] = new DirectAccountsServiceCommunication
  }


  args match {
    case Array() => {
      signUpFlowWithInteraction
      Thread.sleep(1000)
      updateMailFlowWithInteraction
    }

    case Array(name, userName, password, mail, newMail) => {

      val details = UserSignupDetails.tupled((name, userName, password, mail))
      signUpFlow(details)

      Thread.sleep(10000)
      cccAccount.dataStore.getAccount map{ account =>
      updateMailFlow((account.id, newMail))}
    }
  }

}
