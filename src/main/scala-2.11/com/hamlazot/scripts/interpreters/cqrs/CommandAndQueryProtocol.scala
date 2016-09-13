package com.hamlazot
package scripts.interpreters.cqrs

import java.util.UUID
import domain.impl.model.AccountModel.{UserAccount, UserToken}

/**
 * @author yoav @since 9/11/16.
 */
object CommandAndQueryProtocol {

  case class StoreAccountEvent(userAccount: UserAccount, token: UserToken) extends AccountEvent(userAccount.id)

  case class SearchAccountById(accountId: UUID) extends AccountSnapshotQuery(accountId)

  class AccountSnapshotQuery(val id: UUID)

  class AccountEvent(val id: UUID)

  case class AccountPersisted(accountId: UUID)

  case class InvalidCommand(cmd: Any)

  case class AccountDoesNotExist(id: UUID)
}
