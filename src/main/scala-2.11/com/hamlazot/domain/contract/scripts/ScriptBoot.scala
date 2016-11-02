package com.hamlazot.domain.contract.scripts

import scala.concurrent.ExecutionContext


/**
 * @author yoav @since 10/30/16.
 *
 */
object ScriptBoot extends App with Scenarios {

  override implicit val ctxt: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  //  val createAndGetUser = for{
  //    id <- createUser(CreateUserReq("yoav", Nil))
  //    yoav <- getUser(id)
  //  } yield yoav
  //  //createAndGetUser.map(println)

  val prod = for {
    yoav <- createAccountAndUser("Yoav", "Sadeh", Nil)
    jbabo <- createAccountAndUserWithTrustees("Jbabo", "Elmekayes", yoav.user)
    product <- createProduct(jbabo, "Soap", "Well, it's a goddam soap!!!", "hygine")
    _ <- createProductNotification(jbabo, product.productId)
  } yield (yoav, jbabo, product)

  println(prod)
}





