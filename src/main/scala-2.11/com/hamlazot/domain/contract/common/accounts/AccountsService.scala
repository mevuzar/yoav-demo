package com.hamlazot.domain.contract.common.accounts

import com.hamlazot.{CommonOperations, CommonTerms}

import scala.util.Try


/**
 * @author yoav @since 6/21/16.
 */
trait AccountsService extends CommonOperations with CommonTerms with AccountAggregate{

  def signUp: Operation[SignUpRequest, Account]

  def signIn: Operation[SignInRequest, Try[AuthenticationToken]]

  def signOut: Operation[SignOutRequest, Boolean]

  def changeMailAddress: Operation[UpdateMailRequest, Account]

  def getAccount: Operation[GetAccountRequest, Account]
}
