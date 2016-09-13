package com.hamlazot
package domain.impl
package server.accounts.dal

import model.AccountModel.UserAccount
import java.util.UUID
import DataDSL._
import scala.util.Try
import scalaz.Free

/**
 * @author yoav @since 7/11/16.
 */
object AccountRepositoryF {


  /**
   * @author yoav @since 7/10/16.
   */


  object DSL {

    //type FreeCall[A] = Free[DataCall, A]

    sealed trait AccountDataCall[+A] extends DataCall[A]

    final case class AccountQuery(id: UUID) extends AccountDataCall[FutureStringOr[UserAccount]]

    final case class StoreAccount(account: UserAccount) extends AccountDataCall[FutureStringOr[Unit]]

    final case class UpdateMail(id: UUID, mail:String) extends AccountDataCall[FutureStringOr[UserAccount]]

    final case class RefreshUserToken(id: UUID) extends AccountDataCall[FutureStringOr[UUID]]

    final case class DeleteAccount(id: UUID) extends AccountDataCall[FutureStringOr[Unit]]

  }

  object AccountDataOperations extends DataOperations {

    import DSL._

    def query(id: UUID) = dataOperation(AccountQuery(id))

    def store[A](userAccount: UserAccount) = dataOperation(StoreAccount(userAccount))

    def refreshToken[A](id: UUID) = dataOperation(RefreshUserToken(id))

    def updateMail[A](id: UUID, mail: String) = dataOperation(UpdateMail(id, mail))

    def delete[A](id: UUID) = dataOperation(DeleteAccount(id))

  }

}


