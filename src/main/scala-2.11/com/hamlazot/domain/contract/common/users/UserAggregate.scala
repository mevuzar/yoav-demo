package com.hamlazot.domain.contract.common.users

/**
 * @author yoav @since 10/11/16.
 */
trait UserAggregate {
  type CreateUserRequest
  type User
  type UserId
  type Trusters
  type Trustees
  type AddTrusteesRequest
  type AddTrustersRequest
  type RemoveTrusteesRequest
  type RemoveTrustersRequest

}
