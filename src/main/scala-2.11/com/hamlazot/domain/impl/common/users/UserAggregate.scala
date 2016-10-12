package com.hamlazot.domain
package impl.common.users

import com.hamlazot.domain.impl.common.users.UsersModel.RelationType.RelationType
import contract.common.users.{UserAggregate => UAG}
import java.util.UUID
/**
 * @author yoav @since 10/11/16.
 */
trait UserAggregate extends UAG {
  override type CreateUserRequest = this.type
  override type AddTrustersRequest = this.type
  override type Trustees = this.type
  override type RemoveTrusteesRequest = this.type
  override type Trusters = this.type
  override type User = this.type
  override type RemoveTrustersRequest = this.type
  override type UserId = this.type
  override type AddTrusteesRequest = this.type


}

object UsersModel{
  case class User(id: UUID, name: String, familyName: String, trustees: List[RelatedUser])
  case class RelatedUser(id:UUID, relationType: RelationType)
  object RelationType extends Enumeration{
    type RelationType = Value
    val FRIEND, FAMILY, COLLEAGUE, CYBER_FRIEND = Value
  }

}