package com.hamlazot.domain.contract.scripts.users

import java.util.UUID

import com.hamlazot.domain.contract.common.users.UsersAggregate

/**
 * @author yoav @since 10/30/16.
 */
trait ScriptUsersAggregate extends UsersAggregate{
  import ScriptUsersModel._

  override type Trustees = List[User]
  override type Trusters = List[User]
  override type User = AUser
  override type UserId = UUID
}

object ScriptUsersAggregate extends ScriptUsersAggregate

object ScriptUsersModel{
  case class CreateUserReq(name: String, trustees: List[AUser])
  case class AUser(userId: UUID, name: String, trustees: List[AUser], trusters: List[AUser] = Nil)
}