package com.hamlazot.implementation.interpreters

import com.hamlazot.FutureStringOr
import com.hamlazot.ServiceDSL.{ServerCall, ServiceOperation}
import com.hamlazot.domain.contract.common.accounts.AccountsCommunicationF
import AccountsCommunicationF.AccountsMethodCall
import com.hamlazot.domain.impl.common.accounts.AccountsServiceProduction

import scala.concurrent.Future
import scalaz.{Id, ~>}

/**
 * @author yoav @since 9/12/16.
 */
object ServiceCallLogger extends (ServiceOperation ~> Id.Id) {

  override def apply[A](fa: ServiceOperation[A]): Id.Id[A] = fa match {
    case ServerCall(t: AccountsMethodCall[FutureStringOr[_]]) => Left(Future.successful(t.toString))
  }
}
