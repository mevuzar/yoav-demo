package com.hamlazot
package domain.contract.common.users

import com.hamlazot.{AuthenticatedOperations, CommonTerms}

/**
 * Created by Owner on 9/30/2016.
 */
private[domain] trait UsersService extends UserAggregate with CommonOperations{

  def createUser: Operation[CreateUserRequest, UserId]

  def getUser: Operation[UserId, User]

  def deleteUser: Operation[UserId, Boolean]

  def addTrustees: Operation[AddTrusteesRequest, Boolean]

  def addTrusters: Operation[AddTrustersRequest, Boolean]

  def removeTrustees: Operation[RemoveTrusteesRequest, Boolean]

  def removeTrusters: Operation[RemoveTrusteesRequest, Boolean]

}

