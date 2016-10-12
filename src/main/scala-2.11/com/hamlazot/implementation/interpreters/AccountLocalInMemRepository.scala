package com.hamlazot.implementation.interpreters

import java.util.concurrent.atomic.AtomicReference
import java.util.function.UnaryOperator
import com.hamlazot.domain.contract.client.accounts.AccountLocalRepository
import com.hamlazot.domain.impl.common.accounts.AccountModel
import AccountModel.UserAccount

import scala.util.{Success, Try}

/**
 * @author yoav @since 7/20/16.
 */
object AccountLocalInMemRepository extends AccountLocalRepository[UserAccount] {

  private val accountOpt = new AtomicReference[Option[UserAccount]]

  override def getAccount: Option[UserAccount] = accountOpt.get

  override def storeAccount(account: UserAccount): Try[Unit] = Success(accountOpt.set(Some(account)))

  override def updateAccount(f: (UserAccount) => UserAccount): Try[Unit] = {
    Try {
      accountOpt.getAndUpdate(new UnaryOperator[Option[UserAccount]] {
        override def apply(t: Option[UserAccount]): Option[UserAccount] = Some(f(t.getOrElse(throw AccountDoesNotExistException)))
      })
    }
  }

}
