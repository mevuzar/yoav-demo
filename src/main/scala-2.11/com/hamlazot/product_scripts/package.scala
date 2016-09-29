package com.hamlazot

import akka.actor.ActorSystem
import com.hamlazot.DataDSL.DataStoreRequest
import com.hamlazot.app.AccountNodeBoot
import com.hamlazot.implementation.cqrs.AccountsRepositoryCQRSInterpreter
import com.hamlazot.implementation.interpreters.{AccountsRepositoryInMemInterpreter, AccountsServiceProduct}
import com.typesafe.config.ConfigFactory
import domain.contract.client.AccountLocalRepository
import domain.impl.model.AccountModel.{UserToken, UserAccount}

import scalaz.{Id, ~>, Reader}

/**
 * @author yoav @since 9/9/16.
 */
package object product_scripts {

  trait Environment {
    def getToken: Reader[AccountLocalRepository[UserAccount], UserToken]
  }
  val config = ConfigFactory.load

  def getRepo: ~>[DataStoreRequest, Id.Id] = {
    val persistenceTypePath = "hamlazot.business_scripts.persistence"
    val persistenceType = if(config.hasPath(persistenceTypePath))
      PersistenceTypePath(config.getString(persistenceTypePath))
    else
      PersistenceTypePath.NONE

    persistenceType match{
      case PersistenceTypePath.CQRS =>
        val conf =
          """akka.remote.netty.tcp.hostname="127.0.0.1"
       akka.remote.netty.tcp.port=0
          """.stripMargin

        val tempCommunicationSystem = ActorSystem("accounts-service", ConfigFactory.parseString(conf))
        import tempCommunicationSystem.dispatcher
        val (viewRegion, writerRegion) = AccountNodeBoot.getRegionActors(tempCommunicationSystem)

        val repo = new AccountsRepositoryCQRSInterpreter(writerRegion, viewRegion)
        repo

      case PersistenceTypePath.PLAIN | PersistenceTypePath.NONE => AccountsRepositoryInMemInterpreter
    }

  }

}

object PersistenceTypePath extends Enumeration{
  type PersistenceTypePath = Value
  val CQRS, PLAIN, NONE = Value
  def apply(name: String): PersistenceTypePath = {
   val optMatch = values.find(_.toString.toLowerCase == name.toLowerCase)
    if (optMatch.isDefined)
      optMatch.get
    else
      NONE
  }
}