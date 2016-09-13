package com.hamlazot
package scripts.interpreters

import java.util.UUID

import akka.actor.ActorSystem
import com.hamlazot.ServiceDSL.ServiceOperation
import com.hamlazot.domain.impl.client.accounts.interpreter.ClientAccountsProductionService
import com.hamlazot.domain.impl.model.AccountModel.UserAccount

import scala.concurrent.{Future, ExecutionContext}
import scalaz.{Id, ~>}

/**
 * @author yoav @since 7/20/16.
 */
object UserAccounts extends ClientAccountsProductionService {

  implicit val accountsService = AccountsServiceProduct
  val system = ActorSystem("UserAccounts")
  override implicit val ctxt: ExecutionContext = system.dispatcher
  override val accountService: ~>[ServiceOperation, Id.Id] = new DirectAccountsServiceCommunication
  override val serviceCallLogger: ~>[ServiceOperation, Id.Id] = ServiceCallLogger

  override val dataStore = AccountLocalInMemRepository

}