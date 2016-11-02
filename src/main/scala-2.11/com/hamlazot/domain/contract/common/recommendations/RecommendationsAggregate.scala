package com.hamlazot.domain.contract.common.recommendations

/**
 * @author yoav @since 10/31/16.
 */
trait RecommendationsAggregate {
  type Recommendation <: {
    val recommendationId: RecommendationId
    val recommendingUserId: RecommendingUserId
    val recommendedEntityId: RecommendedEntityId
    val recommendedEntityType: RecommendedEntityType
    val rating: Rating
  }

  type RecommendedEntityId
  type RecommendedEntityType
  type RecommendingUserId
  type RecommendationId
  type Rating
}
