package com.hamlazot

import domain.contract.client.AccountLocalRepository
import domain.impl.model.AccountModel.{UserToken, UserAccount}

import scalaz.Reader

/**
 * @author yoav @since 9/9/16.
 */
package object product_scripts {

  trait Environment {
    def getToken: Reader[AccountLocalRepository[UserAccount], UserToken]
  }

}
