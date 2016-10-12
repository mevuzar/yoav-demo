package com.hamlazot.domain.contract.client.users

import scala.util.Try

/**
 * @author yoav @since 10/11/16.
 */

trait UsersLocalRepository[User] {
  def getUser: Option[User]

  def storeUser(user: User): Try[Unit]

  def updateUser(f: User => User): Try[Unit]

  case object UserDoesNotExistException extends Exception("User doesn't exist")

}