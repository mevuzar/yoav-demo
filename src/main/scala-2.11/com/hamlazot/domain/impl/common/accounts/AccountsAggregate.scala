package com.hamlazot.domain
package impl.common.accounts


import java.util.UUID

import com.hamlazot.domain.impl.common.accounts.AccountModel.{UserAccount, AccountCredentials, UserSignupDetails}
import contract.common.accounts.{AccountAggregate => AAG}
/**
 * @author yoav @since 10/15/16.
 */
trait AccountsAggregate extends AAG{

  override type SignUpRequest = UserSignupDetails
  override type SignOutRequest = UUID
  override type SignInRequest = (UUID, AccountCredentials)
  override type Account = UserAccount
  override type UpdateMailRequest = (UUID,String)
  override type GetAccountRequest = UUID


}
