package com.hamlazot.domain.contract.client.users

import com.hamlazot.domain.contract.common.users.UsersAggregate

/**
 * @author yoav @since 11/2/16.
 */
trait UsersProtocol[A <: UsersAggregate] {
  val aggregate: A

  case class CreateUserRequest(name: String, trustees: aggregate.Trustees)
  case class CreateUserResponse(userId: aggregate.UserId)
  case class GetUserRequest(userId: aggregate.UserId)
  case class GetUserResponse(user: aggregate.User)
  case class DeleteUserRequest(userId: aggregate.UserId)
  case class DeleteUserResponse(success: Boolean)
  case class AddTrusteesRequest(userId: aggregate.UserId, trustees: aggregate.Trustees)
  case class AddTrusteesResponse(success: Boolean)
  case class AddTrustersRequest(userId: aggregate.UserId, trusters: aggregate.Trusters)
  case class AddTrustersResponse(success: Boolean)
  case class RemoveTrusteesRequest(userId: aggregate.UserId, trustees: aggregate.Trustees)
  case class RemoveTrusteesResponse(success: Boolean)
  case class RemoveTrustersRequest(userId: aggregate.UserId, trusters: aggregate.Trusters)
  case class RemoveTrustersResponse(success: Boolean)

}
