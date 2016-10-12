package com.hamlazot
package implementation.interpreters

import akka.actor.ActorSystem
import com.hamlazot.ServiceDSL.ServiceOperation
import com.hamlazot.domain.impl.client.accounts.ClientAccountsProductionService
import com.hamlazot.domain.impl.common.accounts.AccountsServiceProduction

import scala.concurrent.ExecutionContext
import scalaz.{Id, ~>}

/**
 * @author yoav @since 7/20/16.
 */
trait ClosedCircuitClientAccount extends ClientAccountsProductionService {

  val system = ActorSystem("accounts-service")
  override implicit val ctxt: ExecutionContext = system.dispatcher

  override val serviceCallLogger: ~>[ServiceOperation, Id.Id] = ServiceCallLogger

  override val dataStore = AccountLocalInMemRepository

}