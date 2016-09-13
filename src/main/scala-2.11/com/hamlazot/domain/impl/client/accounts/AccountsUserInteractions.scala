package com.hamlazot
package domain
package impl
package client.accounts

import model.AccountModel.{AccountCredentials, UserSignupDetails}
import scalaz.{Free, Id, ~>}
import contract.client.PreDefInteractions.StringInteraction
import contract.client.Question
import contract.client.UserInteractions.interact

/**
 * @author yoav @since 7/16/16.
 */
trait AccountsUserInteractions {

  protected def askName = interact(StringInteraction("what's your name?"))

  protected def askUserName = interact(StringInteraction("please enter a user name"))

  protected def askPassword = interact(StringInteraction("please enter a password"))

  protected def askMail = interact(StringInteraction("please enter a mail address"))

  def askSignUpDetails(interpreter: Question ~> Id.Id) = {
    val script = for {
      name <- askName
      userName <- askUserName
      password <- askPassword
      mail <- askMail
    } yield (name, userName, password, mail)

    val interpreted = Free.runFC(script)(interpreter)
    UserSignupDetails.tupled(interpreted)
  }

  def askAccountCredentials(interpreter: Question ~> Id.Id) = {
    val script = for {
      userName <- askUserName
      password <- askPassword
    } yield (userName, password)

    val interpreted = Free.runFC(script)(interpreter)
    AccountCredentials.tupled(interpreted)
  }

  def askMailAddress(interpreter: Question ~> Id.Id) = {
    val script = for {
      mail <- askMail
    } yield mail

    val interpreted = Free.runFC(script)(interpreter)
    interpreted
  }

}
