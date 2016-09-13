package com


import scalaz.Coyoneda
import scalaz.Free
import scala.concurrent.Future

/**
 * @author yoav @since 9/9/16.
 */
package object hamlazot {

  trait CommonOperations {
    type Operation[A, B]
  }

  trait AuthenticatedOperations {
    type AuthenticatedOperation[A, B]
  }

  trait CommonTerms {
    type AuthenticationToken
  }

  object DataDSL {

    sealed trait DataStoreRequest[A]

    trait DataCall[+A]

    final case class DataOpteration[A, Call[+A] <: DataCall[A]](dataCall: Call[A]) extends DataStoreRequest[A]

    trait DataOperations {
      def dataOperation[A, Call[+A] <: DataCall[A]](service: Call[A]): Free[Fetchable, A] =
        Free.liftFC(DataOpteration(service): DataStoreRequest[A])
    }

    type Fetchable[A] = Coyoneda[DataStoreRequest, A]
  }

  object ServiceDSL {

    sealed trait ServiceOperation[A]

    trait ServiceMethodCall[+A]

    final case class ServerCall[A,MethodCall[A] <: ServiceMethodCall[A]](methodCall: MethodCall[A]) extends ServiceOperation[A]

    type Servable[A] = Coyoneda[ServiceOperation, A]

    object ServiceOperations {
      def serviceOperation[A, MethodCall[A] <: ServiceMethodCall[A]](methodCall: MethodCall[A]): Free[Servable, A] =
        Free.liftFC(ServerCall(methodCall): ServiceOperation[A])
    }

  }


  type StringOr[A] = Either[String, A]
  type FutureStringOr[A] = Either[Future[String], Future[A]]
}
