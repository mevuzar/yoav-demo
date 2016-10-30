package com.hamlazot
package domain
package impl.common.users

import java.util.UUID
import DataDSL.DataStoreRequest
import com.typesafe.scalalogging.LazyLogging
import impl.common.users.UsersModel.UserCreationDetails
import contract.common.users.{UsersService, UsersRepositoryF}


import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import scalaz.{Id, ~>}

/**
 * @author yoav @since 10/11/16.
 */
trait UsersProductionService extends UsersService  with UserAggregate with LazyLogging{
  protected val dbDriver: (DataStoreRequest ~> Id.Id)
  protected val dbLogger: (DataStoreRequest ~> Id.Id)
  protected implicit val executionContext: ExecutionContext
  protected implicit val repository: UsersRepositoryF with UserAggregate
  import repository.UsersDataOperations._
//
  override type Operation[A, B] = A => Future[B]

  override def createUser: Operation[UserCreationDetails, UUID] = { request=>
    Future{
      val user = UsersModel.User(UUID.randomUUID(), request.name, request.familyName, request.trustees)

      user.id
    }
  }

  protected def storeUser(user: User): Future[User] = {
    val stored = store(user)
    val dbLog = runFC(stored)(dbLogger).left.get
    dbLog.map(log => {

      logger.info(log)
    })

    val dbResult = runFC(stored)(dbDriver).right.get
    val res = dbResult.map(x => user)
    res
  }

  protected def queryAccount(userId: UUID): Future[User] = {
    val theQuery = query(userId)
    val dbResult = runFC(theQuery)(dbDriver).right.get
    dbResult
  }
}

