package com.hamlazot.app.cqrs

import akka.actor.{ActorRef, ActorIdentity, Identify, Props, ActorPath, ActorSystem}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import akka.persistence.journal.leveldb.{SharedLeveldbJournal, SharedLeveldbStore}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
import akka.pattern.ask

/**
 * @author yoav @since 9/13/16.
 */

object AccountNodeBoot extends App{

  if (args.isEmpty)
    startup(Seq("2551", "2552", "0"))
  else
    startup(args)


  def startup(ports: Seq[String]): Unit = {
    ports foreach { port =>
      // Override the configuration of the port
      val config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
        withFallback(ConfigFactory.load())

      // Create an Akka system
      val system = ActorSystem("accounts-service", config)

      startupSharedJournal(system, startStore = (port == "2551"), path =
        ActorPath.fromString("akka.tcp://accounts-service@127.0.0.1:2551/user/store"))

      getRegionActors(system)

//      if (port != "2551" && port != "2552")
//        system.actorOf(Props[Bot], "bot")
    }

    def startupSharedJournal(system: ActorSystem, startStore: Boolean, path: ActorPath): Unit = {
      // Start the shared journal one one node (don't crash this SPOF)
      // This will not be needed with a distributed journal
      if (startStore)
        system.actorOf(Props[SharedLeveldbStore], "store")
      // register the shared journal
      import system.dispatcher
      implicit val timeout = Timeout(15.seconds)
      val f = (system.actorSelection(path) ? Identify(None))
      f.onSuccess {
        case ActorIdentity(_, Some(ref)) => SharedLeveldbJournal.setStore(ref, system)
        case _ =>
          system.log.error("Shared journal not started at {}", path)
          system.terminate()
      }
      f.onFailure {
        case _ =>
          system.log.error("Lookup of shared journal at {} timed out", path)
          system.terminate()
      }
    }

  }

  def getRegionActors(system: ActorSystem): (ActorRef, ActorRef) = {
    val viewRegion = ClusterSharding(system).start(
      typeName = AccountView.shardName,
      entityProps = AccountView.props(),
      settings = ClusterShardingSettings(system),
      extractEntityId = AccountView.idExtractor,
      extractShardId = AccountView.shardResolver)
    val writerRegion = ClusterSharding(system).start(
      typeName = AccountWriter.shardName,
      entityProps = AccountWriter.props(viewRegion),
      settings = ClusterShardingSettings(system),
      extractEntityId = AccountWriter.idExtractor,
      extractShardId = AccountWriter.shardResolver)
    (viewRegion, writerRegion)
  }
}