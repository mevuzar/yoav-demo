package com.hamlazot.domain.contract
package client.accounts

import common.accounts.AccountAggregate

/**
 * @author yoav @since 10/11/16.
 */
trait ClientAccountsAggregate extends AccountAggregate{
  type Mail
  type AccountId
  override type UpdateMailRequest = (AccountId, Mail)

}
