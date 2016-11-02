package com.hamlazot.domain.contract.common.products

/**
 * @author yoav @since 10/31/16.
 */
trait ProductsAggregate {
  type Product <: {
    val productId: ProductId
    val productName: ProductName
    val productCategory: ProductCategory
    val description: ProductDescription
  }
  type ProductId
  type ProductDescription
  type ProductName

  type ProductCategory <: {
    val productCategoryId: ProductCategoryId
    val productCategoryName: ProductCategoryName
  }
  type ProductCategoryId
  type ProductCategoryName
  type ProductUser

}



