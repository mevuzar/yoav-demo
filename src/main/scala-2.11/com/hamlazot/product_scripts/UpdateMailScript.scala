package com.hamlazot.product_scripts

import com.hamlazot.domain.impl.client.accounts.AccountsUserInteractions
import com.hamlazot.implementation.interpreters.ClosedCircuitClientAccount
import java.util.UUID
/**
 * @author yoav @since 9/12/16.
 */
trait UpdateMailScript {

  object AccountsUserInteraction extends AccountsUserInteractions


  val closedCircuitClientAccount: ClosedCircuitClientAccount
  private val getTokenAndMailFromUser: String => (java.util.UUID,String) = str => (getToken(), str)
  private val getToken: () => java.util.UUID = () => closedCircuitClientAccount.dataStore.getAccount.fold(UUID.randomUUID){a => a.id}
  lazy val updateMail = closedCircuitClientAccount.changeMailAddress.compose(getTokenAndMailFromUser.compose(AccountsUserInteraction.askMailAddress))
}
