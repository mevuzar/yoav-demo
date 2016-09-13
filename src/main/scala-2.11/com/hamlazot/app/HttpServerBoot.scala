package com.hamlazot.app


import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.hamlazot.DataDSL.DataStoreRequest
import com.hamlazot.app.api.AccountsHttpService
import com.hamlazot.app.cqrs.{AccountNodeBoot, AccountsRepositoryCQRSInterpreter}
import com.hamlazot.domain.impl.server.accounts.interpreter.AccountsServiceProduction
import com.hamlazot.scripts.interpreters.{AccountsRepositoryLoggerInterpreter, AccountsServiceProduct}
import com.typesafe.config.{ConfigFactory, Config}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext
import scalaz.{Id, ~>}

/**
 * @author yoav @since 9/12/16.
 */
object HttpServerBoot extends App with LazyLogging {

  logger.debug("Starting accounts-service")

  private val argumentsError = """
   Please run the service with the required arguments: " <httpIpAddress>" <httpPort> "<akkaHostIpAddress>" <akkaport> """


  val conf =
    """akka.remote.netty.tcp.hostname="%hostname%"
       akka.remote.netty.tcp.port=%port%
    """.stripMargin


  assert(args.length == 4, argumentsError)

  val httpHost = args(0)
  val httpPort = args(1).toInt

  val akkaHostname = args(2)
  val akkaPort = args(3).toInt
  val config = ConfigFactory.parseString(conf.replaceAll("%hostname%", akkaHostname)
    .replaceAll("%port%", akkaPort.toString)).withFallback(ConfigFactory.load())
  implicit val system: ActorSystem = ActorSystem("accounts-service", config)
  implicit val materializer = ActorMaterializer()
  val accountsHandler = new AccountsHttpServiceProvider(httpHost, httpPort, akkaHostname, akkaPort)
  Http().bindAndHandle(accountsHandler.route, "0.0.0.0", 8080)
  //Http().bind("0.0.0.0", 8080).runForeach(conn => conn.flow.join(accountsHandler.route).run)
}

class AccountsHttpServiceProvider(httpHost: String,
                                  httpPort: Int,
                                  akkaHostname: String,
                                  akkaPort: Int)(implicit val system: ActorSystem) extends AccountsHttpService {


  val (viewRegion, writerRegion) = AccountNodeBoot.getRegionActors(system)
  override val accountService: AccountsServiceProduction = new AccountsServiceProduction {
    override val dbLogger: ~>[DataStoreRequest, Id.Id] = AccountsRepositoryLoggerInterpreter
    override val dbDriver: ~>[DataStoreRequest, Id.Id] = new AccountsRepositoryCQRSInterpreter(writerRegion, viewRegion)
    override implicit val ctxt: ExecutionContext = system.dispatcher

  }

}