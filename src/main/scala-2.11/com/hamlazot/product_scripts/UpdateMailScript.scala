package com.hamlazot.product_scripts

import com.hamlazot.domain.impl.client.accounts.AccountsUserInteractions
import com.hamlazot.implementation.interpreters.UserAccounts
import java.util.UUID
/**
 * @author yoav @since 9/12/16.
 */
object UpdateMailScript {

  object AccountsUserInteraction extends AccountsUserInteractions

  private val getTokenAndMailFromUser: String => (java.util.UUID,String) = str => (getToken(), str)
  private val getToken: () => java.util.UUID = () => UserAccounts.dataStore.getAccount.fold(UUID.randomUUID){a => a.id}
  lazy val updateMail = UserAccounts.changeMailAddress.compose(getTokenAndMailFromUser.compose(AccountsUserInteraction.askMailAddress))
}
