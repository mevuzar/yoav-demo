package com.hamlazot.domain.contract.client.accounts

import com.hamlazot.domain.contract.common.accounts.AccountsService

import scala.util.Try

/**
 * @author yoav @since 7/6/16.
 */
private[domain] trait ClientAccountService extends AccountsService with ClientAccountsAggregate{

  val dataStore: AccountLocalRepository[Account]
}

