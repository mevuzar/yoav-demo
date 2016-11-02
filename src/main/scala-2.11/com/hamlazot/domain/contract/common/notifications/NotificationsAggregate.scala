package com.hamlazot.domain.contract.common.notifications

/**
 * @author yoav @since 10/31/16.
 */
trait NotificationsAggregate {
  type Subscription <: {
    val subscriptionId: SubscriptionId
  }
  type SubscriptionId

  type Notification

  type Event <: {
    val entityType: EntityType
    val entityId: EntityId
    val eventType: EventType
  }
  type EntityId
  type EntityType
  type UserId <: EntityId
  type EventType
}
