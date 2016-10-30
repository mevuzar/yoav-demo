package com.hamlazot.domain.contract.common.accounts

import java.util.UUID

import com.hamlazot.DataDSL.{DataCall, DataOperations}
import com.hamlazot.FutureStringOr
import com.hamlazot.domain.impl.common.accounts.AccountModel.UserAccount

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

    final case class AccountQueryex(id: UUID) extends AccountDataCall[FutureStringOr[UserAccount]]

    final case class StoreAccount(account: UserAccount) extends AccountDataCall[FutureStringOr[Unit]]

    final case class UpdateMail(id: UUID, mail:String) extends AccountDataCall[FutureStringOr[UserAccount]]

    final case class RefreshUserToken(id: UUID) extends AccountDataCall[FutureStringOr[UUID]]

    final case class DeleteAccount(id: UUID) extends AccountDataCall[FutureStringOr[Unit]]

  }

  private[domain] object AccountDataOperations extends DataOperations {

    import DSL._

    def query(id: UUID) = dataOperation(AccountQueryex(id))

    def store[A](userAccount: UserAccount) = dataOperation(StoreAccount(userAccount))

    def refreshToken[A](id: UUID) = dataOperation(RefreshUserToken(id))

    def updateMail[A](id: UUID, mail: String) = dataOperation(UpdateMail(id, mail))

    def delete[A](id: UUID) = dataOperation(DeleteAccount(id))

  }

}


