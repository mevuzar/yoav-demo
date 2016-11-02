package com.hamlazot.domain.contract
package scripts
package accounts

import com.hamlazot.domain.contract.common.accounts.AccountsService
import java.util.UUID

import com.hamlazot.domain.contract.scripts.accounts.ScriptAccountModel.ScriptAccount

import scala.util.{Success, Try}

/**
 * @author yoav @since 10/31/16.
 */
object ScriptAccountService extends AccountsService with ScriptAccountsAggregate{
  override type Operation[A,B] = A => M1[B]
  override type AuthenticationToken = UUID

  override def signUp: ((String, String)) => M1[ScriptAccount] = {request =>
    M1(ScriptAccount(UUID.randomUUID(), request._1, request._2))
  }

  override def changeMailAddress: ((UUID, String)) => M1[ScriptAccount] = {request =>
  M1(ScriptAccount(request._1, "DummyName", request._2))
  }

  override def signIn: ((String, String)) => M1[Try[UUID]] = {request =>
  M1(Success(UUID.randomUUID()))
  }

  override def getAccount: (UUID) => M1[ScriptAccount] = {request =>
  M1(ScriptAccount(request, "DummyName", "DummyEmail"))
  }

  override def signOut: (UUID) => M1[Boolean] = {request =>
  M1(true)
  }
}

