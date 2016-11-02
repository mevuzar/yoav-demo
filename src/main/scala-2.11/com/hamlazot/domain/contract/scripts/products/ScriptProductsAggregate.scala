package com.hamlazot.domain.contract
package scripts.products

import java.util.UUID

import com.hamlazot.domain.contract.common.products.ProductsAggregate

/**
 * @author yoav @since 10/31/16.
 */
trait ScriptProductsAggregate extends ProductsAggregate {


  override type Product = ScriptProductsModel.Product
  override type ProductCategory = ScriptProductsModel.ProductCategory

  override type ProductUser = UUID
  override type ProductCategoryName = String
  override type ProductId = UUID
  override type ProductDescription = String
  override type ProductCategoryId = UUID
  override type ProductName = String //TODO: Add to README that it may be easier to give a name/description
  // type its String type in the abstract aggregate itself if there's no uncertainty about it.

}

object ScriptProductsAggregate extends ScriptProductsAggregate

object ScriptProductsModel {

  case class ProductCategory(productCategoryId: UUID, productCategoryName: String)

  case class Product(productId: UUID,
                     productName: String,
                     productCategory: ProductCategory,
                     description: String)

}