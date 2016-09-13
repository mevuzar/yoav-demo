package com.hamlazot
package scripts

import domain.contract.client.{AccountLocalRepository, Question}
import domain.impl.client.accounts.AccountsUserInteractions
import domain.impl.model.AccountModel.{AccountCredentials, UserAccount, UserToken}
import interpreters.UserAccounts

import scala.concurrent.Future
import scala.util.Try
import scalaz.{Id, ~>}

/**
 * @author yoav @since 7/6/16.
 */
object SignUpScript {

  object AccountsUserInteraction extends AccountsUserInteractions

//  val preSignIn: AccountLocalRepository[UserAccount] => Tuple2[Environment, ~>[Question, Id.Id]] => (java.util.UUID, AccountCredentials) = { repo => { t =>
//    val fs = for {
//      token <- t._1.getToken
//    } yield (token.token, AccountsUserInteraction.askAccountCredentials(t._2))
//
//    val r = fs.run(repo)
//    r
//  }
//  }
//
//  lazy val userInputToSignInRequest: Tuple2[Environment, ~>[Question, Id.Id]] => AccountLocalRepository[UserAccount] => Future[Try[UserToken]] = { t =>
//    val detonator: AccountLocalRepository[UserAccount] => (java.util.UUID, AccountCredentials) = { repository =>
//      val fs = for {
//        token <- t._1.getToken
//      } yield (token.token, AccountsUserInteraction.askAccountCredentials(t._2))
//
//      fs.run(repository)
//    }
//
//  { repo =>
//    val tt = detonator(repo)
//
//    val jaja = UserAccounts.signIn.compose(detonator)
//    jaja(repo)
//  }
//  }

  lazy val userInputToSignUpRequest = UserAccounts.signUp.compose(AccountsUserInteraction.askSignUpDetails)

}
