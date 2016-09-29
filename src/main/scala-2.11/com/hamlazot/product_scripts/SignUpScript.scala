package com.hamlazot
package product_scripts

import com.hamlazot.domain.impl.client.accounts.AccountsUserInteractions
import com.hamlazot.implementation.interpreters.ClosedCircuitClientAccount

/**
 * @author yoav @since 7/6/16.
 */
trait SignUpScript {

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

  val closedCircuitClientAccount: ClosedCircuitClientAccount
  lazy val userInputToSignUpRequest = closedCircuitClientAccount.signUp.compose(AccountsUserInteraction.askSignUpDetails)


}
