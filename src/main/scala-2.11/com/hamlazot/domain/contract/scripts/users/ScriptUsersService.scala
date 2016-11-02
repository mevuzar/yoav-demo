package com.hamlazot.domain.contract.scripts
package users

import java.util.UUID
import com.hamlazot.domain.contract.client.users.UsersProtocol
import com.hamlazot.domain.contract.common.users.UsersService
import com.hamlazot.domain.contract.scripts.users.ScriptUsersModel.{AUser, CreateUserReq}

import scala.collection.mutable

/**
 * @author yoav @since 10/30/16.
 */
object ScriptUsersService extends UsersService[ScriptUsersAggregate] {

  object ScriptUsersProtocol extends UsersProtocol[ScriptUsersAggregate] {
    override val aggregate: ScriptUsersAggregate = ScriptUsersAggregate
  }
  override val protocol: UsersProtocol[ScriptUsersAggregate] = ScriptUsersProtocol
  override val aggregate: ScriptUsersAggregate = ScriptUsersAggregate
  override type Operation[A, B] = A => M1[B]

  private var users = mutable.MutableList.empty[aggregate.User]

  override def createUser: (protocol.CreateUserRequest) => M1[protocol.CreateUserResponse] = {request =>
    val id = UUID.randomUUID()
    users += AUser(id, request.name, request.trustees)
    M1(protocol.CreateUserResponse(id))
  }

  override def getUser: (protocol.GetUserRequest) => M1[protocol.GetUserResponse] = {request =>
    M1(protocol.GetUserResponse(users.find(u => u.userId == request.userId).get))
  }

  override def deleteUser: (protocol.DeleteUserRequest) => M1[protocol.DeleteUserResponse] = {request =>
    val before = users.size
    users = users.filterNot(u => u.userId == request.userId)

    M1(protocol.DeleteUserResponse(before > users.size))
  }

  override def addTrustees: (protocol.AddTrusteesRequest) => M1[Boolean] = {request =>
    var transformed = false
    users.transform { u =>
    if (u.userId == request.userId) {
      transformed = true
      u.copy(trustees = request.trustees)
    }
    else {
      u
    }
  }
    M1(true)
  }

  override def addTrusters: (protocol.AddTrustersRequest) => M1[Boolean] = {request =>
    var transformed = false
    users.transform { u =>
      if (u.userId == request.userId) {
        transformed = true
        u.copy(trusters = request.trusters)
      }
      else {
        u
      }
    }
    M1(transformed)
  }

  override def removeTrustees: (protocol.RemoveTrusteesRequest) => M1[Boolean] = ???

  override def removeTrusters: (protocol.RemoveTrusteesRequest) => M1[Boolean] = ???
}
