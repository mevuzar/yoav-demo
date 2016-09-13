package com.hamlazot.domain.contract.client

import scalaz.{Free, Id, ~>}
import UserInteractions.interact
/**
 * @author yoav @since 7/19/16.
 */
trait UserInteraction {
  def askUser[A](interaction: Interaction[A])(implicit interpreter: Question ~> Id.Id) = {
    val script = interact(interaction)
    val result = Free.runFC(script)(interpreter)
    result
  }
}
