package com.hamlazot.implementation.serialization

import org.json4s.JsonAST.JValue

/**
 * @author yoav @since 2/20/16.
 */
trait SerializationTransformer {
  def transformSerialized(value: JValue): JValue = value
}
