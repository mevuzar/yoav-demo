package com.hamlazot.domain.impl.model

/**
 * @author yoav @since 7/17/16.
 */
object PlaningModel {
  trait WishType
  case object GoalWishType extends WishType
  case object AssignmentWishType extends WishType
  case object ActivityWishType extends WishType

}
