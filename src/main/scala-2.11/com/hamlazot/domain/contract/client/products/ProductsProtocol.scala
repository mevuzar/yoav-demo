package com.hamlazot.domain.contract.client.products

import com.hamlazot.domain.contract.common.products.ProductsAggregate
import com.hamlazot.domain.contract.scripts.notifications.NotificationsModel.EntityType

/**
 * @author yoav @since 10/31/16.
 */
trait ProductsProtocol[A <: ProductsAggregate] {
  val productsAggregate: A

  case class DeleteProductRequest(productUser: productsAggregate.ProductUser
                                  , productId: productsAggregate.ProductId)

  case class DeleteProductResponse()

  case class UpdateProductRequest(productUser: productsAggregate.ProductUser
                                  , productId: productsAggregate.ProductId
                                  , productName: Option[productsAggregate.ProductName]
                                  , productCategory: Option[productsAggregate.ProductCategory]
                                  , description: Option[productsAggregate.ProductDescription])

  case class UpdateProductResponse()

  case class CreateProductRequest(productUser: productsAggregate.ProductUser
                                  , productName: productsAggregate.ProductName
                                  , productCategory: productsAggregate.ProductCategory
                                  , description: productsAggregate.ProductDescription)

  case class CreateProductResponse(productId: productsAggregate.ProductId, productName: productsAggregate.ProductName)

  case class GetProductResponse(product: productsAggregate.Product)

  case class GetProductRequest(productId: productsAggregate.ProductId)

  case object ProductEntityType extends EntityType
}
