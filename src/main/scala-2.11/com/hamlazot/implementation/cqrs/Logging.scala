package com.hamlazot.implementation.cqrs

import com.typesafe.scalalogging.LazyLogging

/**
 * @author yoav @since 9/11/16.
 */
trait Logging extends LazyLogging {

  implicit def toLogging[V](v: V): FLog[V] = FLog(v)

  case class FLog[V](v: V) {
    def logInfo(f: V => String): V = {
      logger.info(f(v));
      v
    }

    def logDebug(f: V => String): V = {
      logger.debug(f(v));
      v
    }

    def logError(f: V => String): V = {
      logger.error(f(v));
      v
    }

    def logWarn(f: V => String): V = {
      logger.warn(f(v));
      v
    }

    def logTest(f: V => String): V = {
      println(f(v));
      v
    }
  }

  case class FLoh[V](v: V) {
    def lohInfo(f: V => String): V = {
      logger.info(f(v));
      v
    }

  }

}
