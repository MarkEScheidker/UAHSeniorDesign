package com.chargerfuel

private val orders: MutableMap<Int, Order> = mutableMapOf()

fun UserSession.placeOrder(): String? {
    val restaurant = RestaurantStorage.getMenu(getCart().values.first().first)
    if (getCart().values.any { RestaurantStorage.getMenu(it.first) != restaurant })
        return "Cart cannot contain items from more than one restaurant."
    if (RestaurantState.getRestaurantState(restaurant.id) != true)
        return "Restaurant is not open at this time."
    SMS.sendOrderConfirm(this.name, orders.size + 1,
        orders.values.filter { it.cart.values.firstOrNull()?.let { RestaurantStorage.getMenu(it.first) } == restaurant }
            .filter { !it.completed }.size * 2 + 10
    )
    orders[orders.size + 1] =
        Order(orders.size + 1, SQLUtils.getPhoneNumber(name) ?: "", name, System.currentTimeMillis(), getCart())
    clearCart()
    return null
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
