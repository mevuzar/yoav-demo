package com.hamlazot.domain.contract.client.accounts

import scala.util.Try

/**
 * @author yoav @since 10/11/16.
 */
trait AccountLocalRepository[Account] {
   def getAccount: Option[Account]

   def storeAccount(account: Account): Try[Unit]

   def updateAccount(f: Account => Account): Try[Unit]

   case object AccountDoesNotExistException extends Exception("account doesn't exist")

 }
