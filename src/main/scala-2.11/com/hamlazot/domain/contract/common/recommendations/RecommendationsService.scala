package com.hamlazot
package domain.contract.common.recommendations

import com.hamlazot.domain.contract.client.recommendations.RecommendationsProtocol

/**
 * @author yoav @since 10/10/16.
 */
trait RecommendationsService[A <: RecommendationsAggregate] extends CommonTerms with CommonOperations {
  val aggregate: A
  val protocol: RecommendationsProtocol[A]

  def recommend: Operation[protocol.RecommendationRequest, protocol.RecommendationResponse]

}
