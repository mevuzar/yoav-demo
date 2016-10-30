package com


import scala.concurrent.Future
import scala.util.Try
import scalaz.{Coyoneda, Free, Monad, ~>}

/**
 * @author yoav @since 9/9/16.
 */
package object hamlazot {

  trait CommonOperations {
    type Operation[A, B]
    type EffectfulOperation[A, B] <: {
      type EffectfulResult[_] <: {
        def map[T](f: _ => T): EffectfulResult[T]
        def flatMap[T](f: _ => EffectfulResult[T]): EffectfulResult[T]
      }
    }

  }

  object AuthenticationDSL {

    trait Authentication

  }

  trait AuthenticatedOperations extends CommonOperations with CommonTerms {
    //TODO: The fact that the call requires authentication should be represented by the DSL.
    //TODO: The implementor of the AuthenticatedOperation should be least bothered by this fact.


    type AuthenticatedOperation[A, B] = AuthenticationToken => Try[Operation[A, B]]

    def authenticated[A, B](token: AuthenticationToken)(operation: Operation[A, B]): Try[Operation[A,B]]

    def authenticate(token: AuthenticationToken): Try[Unit]

    def identify[Id](token: AuthenticationToken)(idExtractor: AuthenticationToken => Id): Try[Id] =
      authenticate(token) map (s => idExtractor(token))


    case class AuthenticationException(failureCause: AuthenticationFailureCause) extends Exception(s"Authentication failed. cause: $failureCause")

    trait AuthenticationFailureCause

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
        liftFC(DataOpteration(service): DataStoreRequest[A])
    }

    type Fetchable[A] = Coyoneda[DataStoreRequest, A]
  }

  object ServiceDSL {

    sealed trait ServiceOperation[A]

    trait ServiceMethodCall[+A]

    final case class ServerCall[A, MethodCall[A] <: ServiceMethodCall[A]](methodCall: MethodCall[A]) extends ServiceOperation[A]

    type Servable[A] = Coyoneda[ServiceOperation, A]

    object ServiceOperations {
      def serviceOperation[A, MethodCall[A] <: ServiceMethodCall[A]](methodCall: MethodCall[A]): Free[Servable, A] =
        liftFC(ServerCall(methodCall): ServiceOperation[A])
    }

  }

  type FreeC[S[_], A] = Free[({type f[x] = Coyoneda[S, x]})#f, A]

  def liftFC[S[_], A](s: S[A]): FreeC[S, A] =
    Free.liftFU(Coyoneda lift s)

  def runFC[S[_], M[_], A](sa: FreeC[S, A])(interp: S ~> M)(implicit M: Monad[M]): M[A] =
    sa.foldMap[M](new (({type λ[α] = Coyoneda[S, α]})#λ ~> M) {
      def apply[A](cy: Coyoneda[S, A]): M[A] =
        M.map(interp(cy.fi))(cy.k)
    })

  type StringOr[A] = Either[String, A]
  type FutureStringOr[A] = Either[Future[String], Future[A]]
}

