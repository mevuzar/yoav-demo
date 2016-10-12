package com.hamlazot.app

import java.util.concurrent.TimeUnit

import akka.NotUsed
import akka.actor.{Actor, ActorIdentity, ActorSystem, Identify, Props}
import akka.pattern.ask
import akka.persistence.journal.leveldb.SharedLeveldbJournal
import akka.persistence.query.PersistenceQuery
import akka.persistence.query.journal.leveldb.scaladsl.LeveldbReadJournal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.util.Timeout
import com.hamlazot.domain.impl.common.accounts.AccountModel
import AccountModel.AccountCredentials
import com.hamlazot.implementation.cqrs.AccountWriter.CreateAccount
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
 * @author yoav @since 9/20/16.
 */
object AccountQuery extends App {

  val config = """
    akka.loglevel = DEBUG
    akka.persistence.journal.plugin = "akka.persistence.journal.leveldb-shared"
    akka.persistence.journal.leveldb-shared.dir = "target/shared-journal"
               """
  val system = ActorSystem("accounts-service", ConfigFactory.parseString(config))
  system.actorOf(Props[QueryActor])
  val timeout = 5 seconds
  implicit val tt = Timeout(5, TimeUnit.SECONDS)
  val storeFuture = (system.actorSelection("akka.tcp://accounts-service@127.0.0.1:2551/user/store") ? Identify(1)).mapTo[ActorIdentity]
    val actor = AccountNodeBoot.getRegionProxyActors(system)._2
    import java.util.UUID
    val token = UUID.randomUUID
    import system.dispatcher
    Range(1, 20) foreach { r =>
      (actor ? CreateAccount(UUID.randomUUID, AccountCredentials("jaja", "1234"), UUID.randomUUID, "name", "mail")) onComplete{
        case Success(success) => println(s"CreateAccount result: $success")
        case Failure(e) => println(s"CreateAccount failure: $e")
      }
    }
}

class QueryActor extends SharedStoreUsage {

  println(s"ACTOR ${self.path} CREATED")

}

trait SharedStoreUsage extends Actor {

  implicit val mat = ActorMaterializer()(context.system)

  override def preStart(): Unit = {
    context.actorSelection("akka.tcp://accounts-service@127.0.0.1:2551/user/store") ! Identify(1)
  }

  def receive = {
    case ActorIdentity(1, Some(store)) =>
      SharedLeveldbJournal.setStore(store, context.system)
      println(s"store: $store")
      val queries = PersistenceQuery(context.system).readJournalFor[LeveldbReadJournal](LeveldbReadJournal.Identifier)
      val src: Source[String, NotUsed] = queries.allPersistenceIds
      src.runForeach(str => println(s"Hello $str"))

  }
}