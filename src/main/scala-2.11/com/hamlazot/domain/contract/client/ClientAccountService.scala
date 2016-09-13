package com.hamlazot.domain.contract.client

import com.hamlazot.domain.contract.server.AccountsService

import scala.util.Try

/**
 * @author yoav @since 7/6/16.
 */
private[domain] trait ClientAccountService extends AccountsService {

  type Mail
  type AccountId
  override type UpdateMailRequest = (AccountId, Mail)

  val dataStore: AccountLocalRepository[Account]
  //def getAccount: Reader[AccountLocalRepository[Account], Account]
}

trait AccountLocalRepository[Account] {
  def getAccount: Option[Account]

  def storeAccount(account: Account): Try[Unit]

  def updateAccount(f: Account => Account): Try[Unit]

  case object AccountDoesNotExistException extends Exception("account doesn't exist")

}