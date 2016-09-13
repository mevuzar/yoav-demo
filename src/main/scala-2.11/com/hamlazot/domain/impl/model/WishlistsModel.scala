package com.hamlazot.domain.impl.model

import java.util.UUID

import com.hamlazot.domain.impl.model.PlaningModel.WishType

/**
 * @author yoav @since 7/17/16.
 */
object WishlistsModel {

  case class Wish(wishId: UUID, wishType: WishType, category: String, description: String)

  case class Wishlist(wishlistId: UUID, accountId: UUID, wishes: List[Wish]) {
    def updateWishes(add: List[Wish], remove: List[Wish]): Wishlist = {
      copy(wishes = (wishes ::: add).filterNot(remove.contains))
    }
  }

  object Wish {
    def generateWish(wishType: WishType, category: String, description: String): Wish = {
      val id = UUID.randomUUID
      Wish(id, wishType, category, description)
    }
  }

  object Wishlist {
    def generateWishlist(accountId: UUID, wishes: List[Wish]): Wishlist = {
      val id = UUID.randomUUID
      Wishlist(id, accountId, wishes)
    }


  }
}
