package com.hamlazot.domain.contract.common.users

import java.util.UUID

import com.hamlazot.DataDSL.{DataCall, DataOperations}
import com.hamlazot._
import com.hamlazot.domain.impl.common.users.UserAggregate

/**
 * Created by Owner on 10/30/2016.
 */
trait UsersRepositoryF extends UserAggregate {

  object DSL {

    //type FreeCall[A] = Free[DataCall, A]

    sealed trait UsersDataCall[+A] extends DataCall[A]

    final case class UserQuery(id: UUID) extends UsersDataCall[FutureStringOr[User]]

    final case class StoreUser(user: User) extends UsersDataCall[FutureStringOr[UUID]]

  }

  private[domain] object UsersDataOperations extends DataOperations {

    import DSL._

    def query(id: UUID) = dataOperation(UserQuery(id))

    def store[A](user: User) = dataOperation(StoreUser(user))

  }

}
