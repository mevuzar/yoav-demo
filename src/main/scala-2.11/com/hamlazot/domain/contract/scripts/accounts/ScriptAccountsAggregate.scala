package com.hamlazot.domain.contract.scripts.accounts

import com.hamlazot.domain.contract.common.accounts.AccountAggregate
import java.util.UUID
/**
 * @author yoav @since 10/31/16.
 */
trait ScriptAccountsAggregate extends AccountAggregate{
  import ScriptAccountModel._
  override type Account = ScriptAccount
  override type SignInRequest = (String, String)
  override type GetAccountRequest = UUID
  override type SignUpRequest = (String, String)
  override type UpdateMailRequest = (UUID, String)
  override type SignOutRequest = UUID
}

object ScriptAccountModel{
  case class ScriptAccount(id: UUID, name: String, mail: String)
}