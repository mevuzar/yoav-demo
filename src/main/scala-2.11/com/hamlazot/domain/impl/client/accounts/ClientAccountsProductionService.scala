package com.hamlazot.domain.impl.client.accounts

import java.util.UUID

import com.hamlazot.ServiceDSL.ServiceOperation
import com.hamlazot.domain.contract.client.accounts.ClientAccountService
import com.hamlazot.domain.impl.common.accounts.{AccountModel, AccountsCommunicationF}
import AccountModel.{AccountCredentials, UserAccount, UserSignupDetails, UserToken}
import com.hamlazot.implementation.cqrs.Logging

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Try}
import scalaz.{Free, Id, ~>}


/**
 * @author yoav @since 7/6/16.
 */
trait ClientAccountsProductionService extends ClientAccountService with ClientAccountsAggregate with Logging {

  override type AuthenticationToken = UserToken

  implicit val ctxt: ExecutionContext

  val accountServiceCommunication: (ServiceOperation ~> Id.Id)
  val serviceCallLogger: (ServiceOperation ~> Id.Id)

  override type Operation[A, B] = A => Future[B]

  override def getAccount: (UUID) => Future[UserAccount] = { request => {
    val script = AccountsCommunicationOperations.getAccount(request)
    val serviceLog = Free.runFC(script)(serviceCallLogger).left.get
    serviceLog.map(log => logger.info(log))

    val result = Free.runFC(script)(accountServiceCommunication).right
    result.get onSuccess {
      case accountTry =>
        dataStore.storeAccount(accountTry)
      case _ =>
    }
    result.get
  }

  }

  override def signUp: (SignUpRequest) => Future[Account] = { request => {
    val script = AccountsCommunicationOperations.signUp(request)

    val serviceLog = Free.runFC(script)(serviceCallLogger).left.get
    serviceLog.map(log => {

      logger.info(log)
    })

    val result = Free.runFC(script)(accountServiceCommunication).right
    result.get onSuccess {
      case accountTry =>
        dataStore.storeAccount(accountTry)
      case _ =>
    }
    result.get
  }
  }

  override def signIn: (SignInRequest) => Future[Try[AuthenticationToken]] = { request => {
    val script = AccountsCommunicationOperations.signIn(request)
    val futureTryToken = Free.runFC(script)(accountServiceCommunication).right.get
    futureTryToken onSuccess {
      case Success(token) => dataStore.updateAccount(a => a.copy(token = token.token))
      case _ =>
    }

    futureTryToken
  }
  }

  override def signOut: (SignOutRequest) => Future[Boolean] = { request => {
    val script = AccountsCommunicationOperations.signOut(request)
    val futureSignOut = Free.runFC(script)(accountServiceCommunication).right.get
    futureSignOut onSuccess {
      case success => dataStore.updateAccount(a => a.copy(token = null)) //TODO: make token Option[UUID]
      case _ =>
    }

    futureSignOut
  }
  }


  override def changeMailAddress: ((UUID, String)) => Future[UserAccount] = { request =>
    val script = AccountsCommunicationOperations.updateMail(request)
    val futureUpdateMail = Free.runFC(script)(accountServiceCommunication).right.get

    futureUpdateMail onSuccess {
      case account => dataStore.updateAccount(a => a.copy(mail = account.mail))
      case _ =>
    }

    futureUpdateMail
  }
}

