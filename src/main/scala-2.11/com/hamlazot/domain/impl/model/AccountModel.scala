package com.hamlazot.domain.impl.model

import java.util.UUID

import scala.util.{Failure, Success, Try}

/**
 * @author yoav @since 7/6/16.
 */
object AccountModel {

  case class UserSignupDetails(name: String, userName: String, password: String, mail: String)

  case class AccountCredentials(userName: String, password: String)

  case class UserAccount(id:UUID, credentials: AccountCredentials, token: UUID, name:String, mail: String)

  object UserAccountFactory{

    def generateAccount(credentials: AccountCredentials, name:String, mail: String): Try[UserAccount] = {

      if(mailIsValid(mail))
        Success(UserAccount(UUID.randomUUID, credentials, UUID.randomUUID, name, mail)) //TODO: implement validation using Applicative of Validations
      else
        Failure(InvalidMailException(mail))
    }

    def mailIsValid(mail: String): Boolean =
      """(?=[^\s]+)(?=(\w+)@([\w\.]+))""".r.findFirstIn(mail) != None

    def revalidateToken(account: UserAccount): UserAccount = account.copy(token = UUID.randomUUID)
  }

  case class InvalidMailException(mail: String) extends Exception(s"mail $mail has an invalid format")

  case class UserToken(accountId: UUID, token: UUID) //TODO: implement token as JWT

}
