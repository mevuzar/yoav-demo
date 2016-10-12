
package com.hamlazot.domain.impl.common.accounts

import java.util.UUID

import com.hamlazot.DataDSL.DataStoreRequest
import com.hamlazot.domain.contract.common.accounts.AccountsService
import com.hamlazot.domain.impl.common.accounts.AccountRepositoryF.AccountDataOperations.{delete, query, store, updateMail}
import AccountModel.{AccountCredentials, InvalidMailException, UserAccount, UserAccountFactory, UserSignupDetails, UserToken}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}
import scalaz.{Free, Id, ~>}


/**
 * @author yoav @since 7/6/16.
 */
trait AccountsServiceProduction extends AccountsService with LazyLogging{

  override type Operation[A, B] = A => Future[B]

  override type AuthenticationToken = UserToken
  override type SignUpRequest = UserSignupDetails
  override type SignOutRequest = UUID
  override type SignInRequest = (UUID, AccountCredentials)
  override type Account = UserAccount
  override type UpdateMailRequest = (UUID,String)
  override type GetAccountRequest = UUID

  protected val dbDriver: (DataStoreRequest ~> Id.Id)
  protected val dbLogger: (DataStoreRequest ~> Id.Id)

  protected implicit val ctxt: ExecutionContext


  override def changeMailAddress: (UpdateMailRequest) => Future[UserAccount] = {request =>{

      if(UserAccountFactory.mailIsValid(request._2)){
        updateAccountMail(request._1, request._2)
      }else{
       Future.failed(InvalidMailException(request._2))
      }
    }
  }

  
  override def signUp: (SignUpRequest) => Future[Account] = {

    request =>
      val future = {

        val tryAccount = UserAccountFactory.generateAccount(AccountCredentials(request.userName, request.password), request.name, request.mail) match{
          case Success(account) => Future.successful(account)
          case Failure(e) => Future.failed(e)
        }

        val operation = for {
          account <- tryAccount
          stored <- storeAccount(account)
        } yield stored

        operation
      }

      future
  }

  override def signIn: (SignInRequest) => Future[Try[AuthenticationToken]] = { signInRequest => {

    val credentials = signInRequest._2
    val accountId = signInRequest._1
    val storedAccount = for {
      account <- queryAccount(accountId)
      stored <- storeAccount(UserAccountFactory.revalidateToken(account))
    } yield stored

    storedAccount.map(a => Success(UserToken(a.id, a.token)))

  }
  }

  override def signOut: (SignOutRequest) => Future[Boolean] = { request =>
    deleteAccount(request).map {
      case a:Unit => true
      case _ => false
    }
  }

  override def getAccount: (GetAccountRequest) => Future[UserAccount] = {request =>{
    queryAccount(request)
  }
  }

  protected def storeAccount(account: UserAccount): Future[UserAccount] = {
    val stored = store(account)
    val dbLog = Free.runFC(stored)(dbLogger).left.get
    dbLog.map(log => {

      logger.info(log)
    })

    val dbResult = Free.runFC(stored)(dbDriver).right.get
    val res = dbResult.map(x => account)
    res
  }

  protected def updateAccountMail(id: UUID, mail: String): Future[UserAccount] = {
    val script = updateMail(id, mail)
    val dbLog = Free.runFC(script)(dbLogger).left.get
    dbLog.map(log => {

      logger.info(log)
    })

    val dbResult = Free.runFC(script)(dbDriver).right.get
    val res = dbResult.map(x => x)
    res
  }

  protected def queryAccount(accountId: java.util.UUID): Future[UserAccount] = {
    val theQuery = query(accountId)
    val dbResult = Free.runFC(theQuery)(dbDriver).right.get
    dbResult
  }

  protected def deleteAccount(accountId: java.util.UUID): Future[Unit] = {
    val theQuery = delete(accountId)
    val dbResult = Free.runFC(theQuery)(dbDriver)
    dbResult.right.get
  }

}
