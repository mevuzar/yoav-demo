package com.hamlazot.implementation.interpreters

import akka.actor.ActorSystem
import com.hamlazot.DataDSL.DataStoreRequest
import com.hamlazot.domain.impl.server.accounts.interpreter.AccountsServiceProduction

import scala.concurrent.ExecutionContext
import scalaz.{Id, ~>}

/**
 * @author yoav @since 7/20/16.
 */
object AccountsServiceProduct extends AccountsServiceProduction {
  val system = ActorSystem("AccountsServiceProduct")
  override val dbDriver: ~>[DataStoreRequest, Id.Id] = AccountsRepositoryInMemInterpreter

  override val dbLogger: ~>[DataStoreRequest, Id.Id] = AccountsRepositoryLoggerInterpreter
  override implicit val ctxt: ExecutionContext = system.dispatcher
}

