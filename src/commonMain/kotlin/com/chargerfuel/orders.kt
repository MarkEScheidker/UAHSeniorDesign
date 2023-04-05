package com.chargerfuel

import kotlinx.serialization.Serializable

@Serializable
data class Order(val id: Int, val phone: String, val user: String, val time: Long, val cart: Map<Int, Pair<Item, Int>>) {
    var completed: Boolean = false
}