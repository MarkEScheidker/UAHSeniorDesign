package com.chargerfuel
import kotlin.collections.MutableMap

private val orders: MutableMap<Int, Order> = mutableMapOf()

fun UserSession.placeOrder():Boolean{
    val restaurant = RestaurantStorage.getMenu(getCart().values.first().first)
    if (getCart().values.any { RestaurantStorage.getMenu(it.first) != restaurant }) {
        return false
    }
    SMS.sendOrderConfirm(this.name,orders.size+1,
        orders.values.filter { it.cart.values.firstOrNull()?.let { RestaurantStorage.getMenu(it.first) } == restaurant }.filter { !it.completed }.size*2 + 10)
    orders[orders.size + 1] = Order(orders.size + 1, SQLUtils.getPhoneNumber(name) ?: "", name, System.currentTimeMillis(), getCart())
    clearCart()
    return true
}


fun getOrders(restaurant: String): List<Order> =
    orders.values.filter {
        it.cart.values.map { RestaurantStorage.getMenu(it.first) }.contains(RestaurantStorage.getMenu(restaurant))
    }.map {
        Order(
            it.id,
            it.phone,
            it.user,
            it.time,
            it.cart.toMap()
                .filter { RestaurantStorage.getMenu(it.value.first) == RestaurantStorage.getMenu(restaurant) }).apply {
                    completed = it.completed
        }
    }

fun completeOrder(id: Int) {
    val order = orders[id]
    order?.completed = true
    if (order != null) {
        SMS.sendOrderReady(order.user, order.id)
    }
}
