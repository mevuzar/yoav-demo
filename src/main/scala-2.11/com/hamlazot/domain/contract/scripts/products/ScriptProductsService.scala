package com.hamlazot.domain.contract
package scripts
package products

import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import com.hamlazot.domain.contract.client.products.ProductsProtocol
import com.hamlazot.domain.contract.common.products.ProductsService
import com.hamlazot.domain.contract.scripts.notifications.NotificationsModel.{Created, EntityType, CRUDEvent}
import com.hamlazot.domain.contract.scripts.notifications.{EventBus, ScriptNotificationsService}
import com.hamlazot.domain.contract.scripts.products.ScriptProductsModel.{Product, ProductCategory}


import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration

/**
 * @author yoav @since 10/31/16.
 */
private[scripts] trait ScriptProductsService extends ProductsService[ScriptProductsAggregate] {

  import ScriptProductsAggregate._
  private var products = mutable.MutableList.empty[Product]
  implicit val ctxt: ExecutionContext


  override type Operation[A, B] = A => M1[B]
  object ScriptProductsProtocol extends ProductsProtocol[ScriptProductsAggregate] {
    override val productsAggregate: ScriptProductsAggregate = ScriptProductsAggregate
  }
  override val productsAggregate: ScriptProductsAggregate = ScriptProductsAggregate
  override val protocol: ProductsProtocol[ScriptProductsAggregate] = ScriptProductsProtocol


  override def getProduct: (protocol.GetProductRequest) => M1[protocol.GetProductResponse] = { request =>
    products.find(p => p.productId == request.productId) match {
      case Some(product) => M1(protocol.GetProductResponse(product))
      case None => M1 (protocol.GetProductResponse (Product (request.productId, "Dummy", ProductCategory (UUID.randomUUID (), "DummyCategory"), "DummyDescription") ) )
    }
  }

  override def updateProduct: (protocol.UpdateProductRequest) => M1[protocol.UpdateProductResponse] = { request =>
    M1(protocol.UpdateProductResponse())
  }

  override def deleteProduct: (protocol.DeleteProductRequest) => M1[protocol.DeleteProductResponse] = { request =>
    M1(protocol.DeleteProductResponse())
  }

  override def createProduct: (protocol.CreateProductRequest) => M1[protocol.CreateProductResponse] = { request =>
    val product = Product(UUID.randomUUID(), request.productName, request.productCategory, request.description)
    products += product
    ActorSystem().scheduler.scheduleOnce(FiniteDuration(7, TimeUnit.SECONDS)) {
      EventBus.queue.put(CRUDEvent(protocol.ProductEntityType, product.productId, product.productName, Created))
    }
    M1(protocol.CreateProductResponse(product.productId, request.productName))
  }
}

object ScriptProductsService extends ScriptProductsService {
  override implicit val ctxt: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

}