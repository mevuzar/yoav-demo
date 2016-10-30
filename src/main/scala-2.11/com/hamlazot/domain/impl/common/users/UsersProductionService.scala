package com.hamlazot
package domain
package impl.common.users

import java.util.UUID
import DataDSL.DataStoreRequest
import impl.common.users.UsersModel.UserCreationDetails
import contract.common.users.UsersService
import impl.common.accounts.AccountModel.UserToken

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import scalaz.{Id, ~>}

/**
 * @author yoav @since 10/11/16.
 */
trait UsersProductionService extends UsersService with UserAggregate {
  protected val dbDriver: (DataStoreRequest ~> Id.Id)
  protected val dbLogger: (DataStoreRequest ~> Id.Id)
  protected implicit val executionContext: ExecutionContext

  override type Operation[A, B] = A => Future[B]

  override def createUser: Operation[UserCreationDetails, UUID] = { credentials=>
    Future(UUID.randomUUID())
  }
}

