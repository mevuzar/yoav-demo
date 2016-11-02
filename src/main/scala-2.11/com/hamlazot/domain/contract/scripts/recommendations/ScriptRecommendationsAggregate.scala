package com.hamlazot.domain.contract.scripts.recommendations

import com.hamlazot.domain.contract.common.recommendations.RecommendationsAggregate
import java.util.UUID

import com.hamlazot.domain.contract.scripts.notifications.NotificationsModel.EntityType

/**
 * @author yoav @since 10/31/16.
 */
trait ScriptRecommendationsAggregate extends RecommendationsAggregate{
  override type RecommendedEntityId = UUID
  override type RecommendationId = UUID
  override type RecommendingUserId = UUID
  override type Rating = RecommendationModel.RatingOutOfFive.RatingOutOfFive
  override type Recommendation = RecommendationModel.Recommendation
  override type RecommendedEntityType = EntityType
}

object RecommendationModel {

  case class Recommendation(recommendationId: UUID, recommendingUserId: UUID, recommendedEntityId: UUID,recommendedEntityType: EntityType, rating: RatingOutOfFive.RatingOutOfFive, description: String)


  object RatingOutOfFive extends Enumeration {
    type RatingOutOfFive = Value
    val ONE, TWO, THREE, FOUR, FIVE = Value
  }

}
object ScriptRecommendationsAggregate extends ScriptRecommendationsAggregate