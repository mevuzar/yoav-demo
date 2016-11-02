package com.hamlazot.domain
package impl.common.users

import com.hamlazot.domain.impl.common.users.UsersModel._
import com.hamlazot.domain.impl.common.users.UsersModel.RelationType.RelationType
import contract.common.users.{UsersAggregate => UAG}
import java.util.UUID
/**
 * @author yoav @since 10/11/16.
 */
trait UserAggregate extends UAG {
  //override type CreateUserRequest = UserCreationDetails
  //override type AddTrustersRequest = (UUID, List[RelatedUser])
  override type Trustees = List[RelatedUser]
  //override type RemoveTrusteesRequest = (UUID, List[RelatedUser])
  override type Trusters = List[RelatedUser]
  override type User = UsersModel.User
  //override type RemoveTrustersRequest = (UUID, List[RelatedUser])
  override type UserId = UUID
  //override type AddTrusteesRequest = (UUID, List[RelatedUser])


}

object UsersModel{
  case class UserCreationDetails(name: String, familyName: String, trustees: List[RelatedUser])
  case class User(id: UUID, name: String, familyName: String, trustees: List[RelatedUser])
  case class RelatedUser(id:UUID, relationType: RelationType)
  object RelationType extends Enumeration{
    type RelationType = Value
    val FRIEND, FAMILY, COLLEAGUE, CYBER_FRIEND = Value
  }


}