package com.hamlazot.domain.contract
package scripts
package recommendations

import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import com.hamlazot.domain.contract.client.recommendations.RecommendationsProtocol
import com.hamlazot.domain.contract.common.recommendations.RecommendationsService
import com.hamlazot.domain.contract.scripts.notifications.NotificationsModel.{CRUDEvent, Created}
import com.hamlazot.domain.contract.scripts.notifications.{EventBus, ScriptNotificationsService}
import com.hamlazot.domain.contract.scripts.recommendations.RecommendationModel.Recommendation

import scala.collection.mutable
import scala.concurrent.duration.FiniteDuration

/**
 * @author yoav @since 10/31/16.
 */
trait ScriptRecommendationsService extends RecommendationsService[ScriptRecommendationsAggregate] {
  override type Operation[A, B] = A => M1[B]
  private val recommendations = mutable.HashMap.empty[UUID, List[aggregate.Recommendation]]
  override val aggregate = ScriptRecommendationsAggregate

  object ScriptRecommendationsProtocol extends RecommendationsProtocol[ScriptRecommendationsAggregate] {
    override val aggregate = ScriptRecommendationsAggregate
  }

  override val protocol:RecommendationsProtocol[ScriptRecommendationsAggregate] = ScriptRecommendationsProtocol

  override def recommend: (protocol.RecommendationRequest) => M1[protocol.RecommendationResponse] = { request =>
    val recommendation = Recommendation(UUID.randomUUID(), request.userId, request.entityId, request.recommendedEntityType, request.rating, request.description)

    if (recommendations.contains(request.entityId)) {
      recommendations.update(request.entityId, recommendations(request.entityId) ::: List(recommendation))
    }
    import scala.concurrent.ExecutionContext.Implicits.global
    ActorSystem().scheduler.scheduleOnce(FiniteDuration(7, TimeUnit.SECONDS)) {
      val recommendationMsg = s"${request.recommendedEntityType} with id ${request.entityId} was recommended by user" +
        s"${request.userId}, with rating of ${request.rating}"
      EventBus.queue.put(CRUDEvent(protocol.RecommendationEntityType, recommendation.recommendedEntityId, recommendationMsg, Created))
      EventBus.queue.put(protocol.EntityRecommendedEvent(protocol.RecommendationEntityType, recommendation.recommendedEntityId, recommendationMsg))
    }

    M1(protocol.RecommendationResponse(recommendation.recommendationId))
  }

}
