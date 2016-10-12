package com.hamlazot.domain.contract.common.accounts

/**
 * @author yoav @since 10/11/16.
 */
trait AccountAggregate {
  type Account
  type SignUpRequest
  type SignInRequest
  type SignOutRequest
  type UpdateMailRequest
  type GetAccountRequest
}
