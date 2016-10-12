package com.hamlazot.domain
package impl.client.accounts

import java.util.UUID

import com.hamlazot.domain.impl.common.accounts.AccountModel
import AccountModel.{UserToken, UserAccount, UserSignupDetails, AccountCredentials}
import contract.client.accounts.{ClientAccountsAggregate => AccountAggregate}

/**
 * @author yoav @since 10/11/16.
 */
trait ClientAccountsAggregate extends AccountAggregate{
  override type Mail = String
  override type AccountId = java.util.UUID
  override type SignOutRequest = UUID
  override type SignInRequest = (UUID, AccountCredentials)
  override type SignUpRequest = UserSignupDetails
  override type Account = UserAccount
  override type GetAccountRequest = UUID

}
