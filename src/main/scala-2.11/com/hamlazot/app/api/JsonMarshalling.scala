package com.hamlazot.app.api

/**
 * @author yoav @since 9/12/16.
 */

import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.{FromRequestUnmarshaller, Unmarshaller}
import akka.stream.Materializer
import org.json4s.DefaultFormats
import org.json4s.ext.JavaTypesSerializers
import org.json4s.jackson.JsonMethods
import org.json4s.jackson.Serialization.write

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/**
 * Created by yoav on 1/18/16.
 */

trait JsonMarshalling extends JsonMethods {
  val jsonMediaType = MediaTypes.`application/json`
  val jsonContentType = ContentTypes.`application/json`

  implicit def jsonFormat = DefaultFormats ++ JavaTypesSerializers.all

  implicit def anyRefMarsheller[A <: AnyRef]: ToEntityMarshaller[A] = {
    Marshaller.withFixedContentType(jsonMediaType) {
      (anyRef: AnyRef) => HttpEntity(ContentType(jsonMediaType), serialize(anyRef))
    }
  }

  implicit def anyValMarsheller[A <: AnyVal]: ToEntityMarshaller[A] = {
    Marshaller.withFixedContentType(jsonMediaType) {
      (anyVal: AnyVal) => HttpEntity(ContentType(jsonMediaType), anyVal.toString)
    }
  }

  implicit def anyRefUnmarshaller[A: Manifest]: FromRequestUnmarshaller[A] = {
    new Unmarshaller[HttpRequest, A] {
      override def apply(value: HttpRequest)(implicit ec: ExecutionContext, materializer: Materializer): Future[A] = {
        value.entity.withContentType(jsonContentType).toStrict(5 seconds).map(_.data.toArray).map(x => {
          deserialize[A](new String(x))
        })
      }
    }
  }

  implicit def anyRefTryMarsheller[A <: AnyRef]: ToEntityMarshaller[Try[A]] = {
    Marshaller.withFixedContentType(jsonMediaType) {
      (anyRef: AnyRef) => HttpEntity(ContentType(jsonMediaType), serialize(anyRef))
    }
  }

  implicit def anyRefTryUnmarshaller[A: Manifest, E <: Enumeration]: FromRequestUnmarshaller[Try[A]] = {
    new Unmarshaller[HttpRequest, Try[A]] {

      override def apply(value: HttpRequest)(implicit ec: ExecutionContext, materializer: Materializer): Future[Try[A]] = {
        value.entity.withContentType(jsonContentType).toStrict(5 seconds).map(_.data.toArray).map(x => {
          Try(deserialize[A](new String(x)))
        })
      }
    }
  }

  def serialize(obj: AnyRef): String = {
    compact(render(parse(write(obj)).snakizeKeys))
  }

  def deserialize[A: Manifest](json: String): A = {
    parse(json).camelizeKeys.extract[A]
  }
}
