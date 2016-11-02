package com.hamlazot.domain.contract.client.recommendations

import java.util.UUID

import com.hamlazot.domain.contract.common.recommendations.RecommendationsAggregate
import com.hamlazot.domain.contract.scripts.notifications.NotificationsModel.{EntityType, Event, EventType}

/**
 * @author yoav @since 10/31/16.
 */
trait RecommendationsProtocol[A <: RecommendationsAggregate] {
  val aggregate: A

  case object RecommendationEntityType extends EntityType
  case object RecommendedEventType extends EventType

  case class EntityRecommendedEvent(entityType: EntityType, entityId: UUID, entityKnownName: String) extends Event {
    override val eventType: EventType = RecommendedEventType
  }
  case class RecommendationRequest(userId: aggregate.RecommendingUserId, entityId: aggregate.RecommendedEntityId, recommendedEntityType: aggregate.RecommendedEntityType, description: String, rating: aggregate.Rating)
  case class RecommendationResponse(recommendationId: aggregate.RecommendationId)
}

