package com.hamlazot.app.api

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes.{Created, InternalServerError, UnprocessableEntity, OK}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.http.scaladsl.server.Directives.{as, decodeRequest, entity}
import akka.http.scaladsl.server.directives.{MethodDirectives, MiscDirectives, PathDirectives, RouteDirectives}
import akka.stream.ActorMaterializer
import com.hamlazot.domain.impl.model.AccountModel.UserSignupDetails
import com.hamlazot.domain.impl.server.accounts.interpreter.AccountsServiceProduction
import com.typesafe.scalalogging.LazyLogging
import org.json4s.MappingException
import java.util.UUID
import scala.util.{Failure, Success, Try}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.ExceptionHandler
import akka.http.scaladsl.server.directives._


/**
 * @author yoav @since 9/12/16.
 */
trait AccountsHttpService extends PathDirectives
with MiscDirectives
with MethodDirectives
with RouteDirectives
with JsonMarshalling
with LazyLogging {


  implicit val system: ActorSystem
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  val accountService: AccountsServiceProduction

  val route = {
    pathPrefix("v1" / "accounts") {
      post {
        decodeRequest {
          entity(as[Try[UserSignupDetails]]) { tryGetUserDetails =>
            tryGetUserDetails match {
              case Success(userDetails) => {
                val response = accountService.signUp(userDetails).map {
                  case account => HttpResponse(Created, entity = HttpEntity(ContentTypes.`application/json`, serialize(account)))
                } recover {
                  case e: Exception =>

                    logger.error("Error while processing account creation request.", e)
                    HttpResponse(InternalServerError)

                  case t: Throwable => throw t
                }

                complete(response)
              }

              case Failure(e) =>
                val msg = "Exception occurred while trying to deserialize request"
                e match {
                  case mex: MappingException =>
                    logger.info(msg, e)
                    complete(HttpResponse(UnprocessableEntity, entity = HttpEntity(ContentTypes.`application/json`, e.getMessage)))
                  case ex: Exception =>
                    logger.error(msg, e)
                    complete(HttpResponse(InternalServerError))
                }

            }

          }
        }
      }
    } ~ get {
      (path(Segment / Segment /Segment)) { (a, b, accountId) =>
          val response = accountService.getAccount(UUID.fromString(accountId)) map{
            case account =>
              HttpResponse(OK, entity = HttpEntity(ContentTypes.`application/json`, serialize(account)))
          } recover{
            case e: Exception =>
              logger.error(s"Exception occurred while trying to get account with id $accountId", e)
              HttpResponse(InternalServerError)
          }

        Thread.sleep(1000)
          complete(response)
        }
      }
  }
}