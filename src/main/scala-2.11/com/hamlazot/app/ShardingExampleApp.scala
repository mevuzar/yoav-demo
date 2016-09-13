package com.hamlazot.app

import akka.actor.{ActorRef, ActorIdentity, ActorPath, ActorSystem, Identify, Props, ReceiveTimeout}
import akka.cluster.sharding.{ClusterShardingSettings, ClusterSharding, ShardRegion}
import akka.pattern.ask
import akka.persistence.PersistentActor
import akka.persistence.journal.leveldb.{SharedLeveldbJournal, SharedLeveldbStore}
import akka.util.Timeout
import com.hamlazot.app.Model.{CounterChanged, Get, EntityEnvelope}
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

/**
 * @author yoav @since 9/11/16.
 */
object ShardingExampleApp extends App {

  val system = ActorSystem("ShardingExampleApp")

  startup(Seq())
  def startup(ports: Seq[String]): Unit = {
    ports foreach { port =>
      // Override the configuration of the port
      val config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
        withFallback(ConfigFactory.load())

      // Create an Akka system
      val system = ActorSystem("ClusterSystem", config)

      startupSharedJournal(system, startStore = (port == "2551"), path =
        ActorPath.fromString("akka.tcp://ClusterSystem@127.0.0.1:2551/user/store"))
    }
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

  object ClusterBoot {
    val counterRegion: ActorRef = ClusterSharding(system).start(
      typeName = "Counter",
      entityProps = Props[Counter],
      settings = ClusterShardingSettings(system),
      extractEntityId = extractEntityId,
      extractShardId = extractShardId)

    val extractEntityId: ShardRegion.ExtractEntityId = {
      case EntityEnvelope(id, payload) ⇒ (id.toString, payload)
      case msg@Get(id) ⇒ (id.toString, msg)
    }

    val numberOfShards = 100

    val extractShardId: ShardRegion.ExtractShardId = {
      case EntityEnvelope(id, _) ⇒ (id % numberOfShards).toString
      case Get(id) ⇒ (id % numberOfShards).toString
    }

  }
}

  object Model {

    case object Increment

    case object Decrement

    final case class Get(counterId: Long)

    final case class EntityEnvelope(id: Long, payload: Any)

    case object Stop

    final case class CounterChanged(delta: Int)

  }
class Counter extends PersistentActor {

  import Model._
  import ShardRegion.Passivate

  context.setReceiveTimeout(120 seconds)

  // self.path.name is the entity identifier (utf-8 URL-encoded)
  override def persistenceId: String = "Counter-" + self.path.name

  var count = 0

  def updateState(event: CounterChanged): Unit =
    count += event.delta

  override def receiveRecover: Receive = {
    case evt: CounterChanged ⇒ updateState(evt)
  }

  override def receiveCommand: Receive = {
    case Increment ⇒ persist(CounterChanged(+1))(updateState)
    case Decrement ⇒ persist(CounterChanged(-1))(updateState)
    case Get(_) ⇒ sender() ! count
    case ReceiveTimeout ⇒ context.parent ! Passivate(stopMessage = Stop)
    case Stop ⇒ context.stop(self)
  }
}

