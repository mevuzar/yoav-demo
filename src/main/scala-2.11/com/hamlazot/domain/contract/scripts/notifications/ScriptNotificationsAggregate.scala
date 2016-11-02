package com.hamlazot.domain.contract.scripts.notifications

import com.hamlazot.domain.contract.common.notifications.NotificationsAggregate
import java.util.UUID
/**
 * @author yoav @since 10/31/16.
 */
trait ScriptNotificationsAggregate extends NotificationsAggregate{
  override type Subscription = NotificationsModel.Subscription
  override type Notification = String
  override type EventType = NotificationsModel.EventType
  override type Event = NotificationsModel.CRUDEvent

  override type EntityType = NotificationsModel.EntityType
  override type SubscriptionId = UUID
  override type EntityId = UUID
  override type UserId = UUID


}

object NotificationsModel{

  trait Event{
    val entityType: EntityType
    val entityId: UUID
    val eventType: EventType
    
  }
  case class CRUDEvent(entityType: EntityType, entityId: UUID, entityKnownName: String, eventType: CRUDEventType) extends Event

  case class Subscription(subscriptionId: UUID, userId: UUID, entityId: UUID, eventType: EventType)

  case class Notification(userId: UUID, entityId: UUID, eventType: EventType)

  trait EventType
  sealed trait CRUDEventType extends EventType
  case object Created extends CRUDEventType
  case object Deleted extends CRUDEventType
  case object Updated extends CRUDEventType

  case object All extends EventType
  //    trait BaseEventType extends Enumeration{
  //      type BaseEventType = Value
  //      val CREATED, DELETE, UPDATED, ALL = Value
  //    }

  trait EntityType

//  object EntityType extends Enumeration{
//    type EntityType = Value
//    val USER, PRODUCT, RECOMMENDATION = Value
//  }

}

object ScriptNotificationsAggregate extends ScriptNotificationsAggregate


