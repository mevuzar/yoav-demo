package com.hamlazot.domain.contract.common.users

import com.hamlazot.{AuthenticatedOperations, CommonTerms}

/**
 * Created by Owner on 9/30/2016.
 */
private[domain] trait UsersService extends AuthenticatedOperations with UserAggregate {

  def createUser: AuthenticatedOperation[CreateUserRequest, UserId]

  def getUser: AuthenticatedOperation[UserId, User]

  def deleteUser: AuthenticatedOperation[UserId, Boolean]

  def addTrustees: AuthenticatedOperation[AddTrusteesRequest, Boolean]

  def addTrusters: AuthenticatedOperation[AddTrustersRequest, Boolean]

  def removeTrustees: AuthenticatedOperation[RemoveTrusteesRequest, Boolean]

  def removeTrusters: AuthenticatedOperation[RemoveTrusteesRequest, Boolean]

}

