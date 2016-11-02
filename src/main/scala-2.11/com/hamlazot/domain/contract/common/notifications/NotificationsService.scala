package com.hamlazot.domain.contract.common.notifications

import com.hamlazot.domain.contract.client.notifications.NotificationsProtocol
import com.hamlazot.{CommonOperations, CommonTerms}

/**
 * @author yoav @since 10/31/16.
 */
trait NotificationsService[A <: NotificationsAggregate] extends CommonOperations with CommonTerms {
  val protocol: NotificationsProtocol
  val aggregate: A

  def subscribe:Operation[protocol.SubscribeRequest, protocol.SubscribeResponse]

  def unsubscribe:Operation[protocol.UnsubscribeRequest, protocol.UnsubscribeResponse]

}
