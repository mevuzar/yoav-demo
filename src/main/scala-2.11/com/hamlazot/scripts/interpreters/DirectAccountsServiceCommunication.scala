package com.hamlazot.scripts.interpreters

import com.hamlazot.ServiceDSL.{ServerCall, ServiceOperation}
import com.hamlazot.domain.impl.common.accounts.communication.AccountsCommunicationF.{UpdateMailCall, AccountsMethodCall, SignInCall, SignOutCall, SignUpCall}
import com.hamlazot.domain.impl.server.accounts.interpreter.AccountsServiceProduction

import scalaz.{Id, ~>}

/**
 * @author yoav @since 7/13/16.
 */
class DirectAccountsServiceCommunication(implicit accountsService: AccountsServiceProduction) extends (ServiceOperation ~> Id.Id) {

  override def apply[A](fa: ServiceOperation[A]): Id.Id[A] = fa match {
    case ServerCall(t: AccountsMethodCall[A]) => t match {
      case SignUpCall(details) => {
        val signUpResult = accountsService.signUp(details)
        Right(signUpResult)
      }
      case SignInCall((id, credentials)) => Right(accountsService.signIn((id, credentials)))
      case SignOutCall(accountId) => Right(accountsService.signOut(accountId))
      case UpdateMailCall(details) => Right(accountsService.changeMailAddress(details._1, details._2))
    }

  }
}



