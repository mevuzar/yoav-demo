package com.hamlazot.domain.impl.common.accounts.communication

import java.util.UUID

import com.hamlazot.FutureStringOr
import com.hamlazot.ServiceDSL.ServiceMethodCall
import com.hamlazot.app.cqrs.AccountWriter.ChangeAccountMail
import com.hamlazot.domain.impl.model.AccountModel.{UserToken, UserAccount, AccountCredentials, UserSignupDetails}
import com.hamlazot.ServiceDSL.ServiceOperations._
import scala.util.Try

/**
 * @author yoav @since 7/13/16.
 */
object AccountsCommunicationF {

  sealed trait AccountsMethodCall[+A] extends ServiceMethodCall[A]

  case class SignUpCall(signUpDetails: UserSignupDetails) extends AccountsMethodCall[FutureStringOr[UserAccount]]

  case class SignInCall(signInDetails: (UUID, AccountCredentials)) extends AccountsMethodCall[FutureStringOr[Try[UserToken]]]

  case class SignOutCall(accountId: UUID) extends AccountsMethodCall[FutureStringOr[Boolean]]

  case class UpdateMailCall(updateMailDetails: (UUID, String)) extends AccountsMethodCall[FutureStringOr[UserAccount]]

  case class GetAccountCall(id: UUID) extends AccountsMethodCall[FutureStringOr[UserAccount]]

  object AccountsCommunicationOperations {
    def signUp(signupDetails: UserSignupDetails) = serviceOperation(SignUpCall(signupDetails))

    def signIn(signInDetails: (UUID, AccountCredentials)) = serviceOperation(SignInCall(signInDetails))

    def signOut(accountId: UUID) = serviceOperation(SignOutCall(accountId))

    def updateMail(updateMailDetails: (UUID, String)) = serviceOperation(UpdateMailCall(updateMailDetails))

    def getAccount(id: UUID) = serviceOperation(GetAccountCall(id))
  }

}
