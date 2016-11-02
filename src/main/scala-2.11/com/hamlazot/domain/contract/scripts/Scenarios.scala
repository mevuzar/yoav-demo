package com.hamlazot.domain.contract
package scripts

import java.util.UUID
import java.util.concurrent.TimeUnit

import com.hamlazot.domain.contract.scripts.accounts.ScriptAccountService
import com.hamlazot.domain.contract.scripts.notifications.NotificationsModel.Created
import com.hamlazot.domain.contract.scripts.notifications.{NotificationBus, ScriptNotificationsService}
import com.hamlazot.domain.contract.scripts.products.ScriptProductsModel.ProductCategory
import com.hamlazot.domain.contract.scripts.products.ScriptProductsService
import com.hamlazot.domain.contract.scripts.users.ScriptUsersModel.AUser
import com.hamlazot.domain.contract.scripts.users.ScriptUsersService

import scala.concurrent.ExecutionContext

/**
 * @author yoav @since 10/31/16.
 */
trait Scenarios {

  implicit val ctxt: ExecutionContext
  import ScriptUsersService.protocol._
  //Scenarios:

  //1. Create account -> Create User  = User UA
  def createAccountAndUser(name: String, mail: String, trustees: List[AUser]) = for {
    account <- ScriptAccountService.signUp(name, mail)
    userIdResponse <- ScriptUsersService.createUser(CreateUserRequest(account.name, trustees))
    user <- ScriptUsersService.getUser(GetUserRequest(userIdResponse.userId))
  } yield user

  //2. Create account -> Create User -> Add Trustees(UA)(Add Trusters to UA) = User UB
  def createAccountAndUserWithTrustees(name: String, mail: String, user: AUser) = for {
    userResponse <- createAccountAndUser(name, mail, List(user))
    result <- ScriptUsersService.addTrusters(AddTrustersRequest(user.userId, List(userResponse.user)))
  } yield userResponse.user

  //3. UB - Create Product = Product PA
  def createProduct(user: AUser, productName: String, productDescription: String, productCategoryName: String) = {
    import ScriptProductsService.protocol._
    val product = ScriptProductsService.createProduct(CreateProductRequest(user.userId, productName, ProductCategory(UUID.randomUUID(), productCategoryName), productDescription))

    product
  }

  //4. UB - Create Product Notification on PA
  def createProductNotification(user: AUser, productId: UUID) = {

    val subscription = ScriptNotificationsService.subscribe(ScriptNotificationsService.protocol.SubscribeRequest(user.userId, productId, Created))
    val notification = NotificationBus.queue.poll(10, TimeUnit.SECONDS)
    println(notification)
    subscription
  }

  /*



  5. UA - Create Recommendation on PA
   */

}
