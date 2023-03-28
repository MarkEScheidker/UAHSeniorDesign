package com.chargerfuel

private val carts: MutableMap<String, Cart> = mutableMapOf()

fun UserSession.addToCart(itemID: Int) = carts.getOrPut(name) { mutableMapOf() }
    .apply { this[itemID] = (this[itemID] ?: 0) + 1 }

fun UserSession.removeFromCart(itemID: Int) =
    carts[name]?.apply { if (this[itemID] == 1) remove(itemID) else this[itemID] = (this[itemID] ?: 2) - 1 }

fun UserSession.clearCart(): Cart? = carts.remove(name)

fun UserSession.getCartSize(): Int = carts.getOrPut(name) { mutableMapOf() }.map { it.value }.sum()

fun UserSession.getCart(): Map<Int, Pair<Item, Int>> = carts[name]
    ?.mapNotNull { (id, count) -> RestaurantStorage.getItem(id)?.let { id to (it to count) } }?.toMap()
    ?: mapOf()

fun UserSession.submitOrder() {
    clearCart()
}