package com.chargerfuel

private val orders: MutableMap<Int, Order> = mutableMapOf()

fun UserSession.placeOrder() {
    orders[orders.size + 1] = Order(orders.size + 1, SQLUtils.getPhoneNumber(name) ?: "", name, System.currentTimeMillis(), getCart())
    clearCart()
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
    orders[id]?.completed = true
}