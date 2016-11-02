package com.hamlazot.domain.contract.client.notifications

import com.hamlazot.domain.contract.common.notifications.NotificationsAggregate

/**
 * @author yoav @since 10/31/16.
 */
trait NotificationsProtocol{

  val aggregate: NotificationsAggregate

  case class SubscribeRequest(
                               userId: aggregate.UserId
                               , entityId: aggregate.EntityId
                               , eventType: aggregate.EventType
                               )

  case class SubscribeResponse(
                                subcriptionId: aggregate.SubscriptionId
                                )

  case class UnsubscribeRequest(
                                 subcriptionId: aggregate.SubscriptionId
                                 )

  case class UnsubscribeResponse(result: Boolean)

}
