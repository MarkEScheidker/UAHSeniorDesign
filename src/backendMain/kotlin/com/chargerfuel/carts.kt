package com.chargerfuel

typealias Cart = MutableMap<Int, Int>

private val carts: MutableMap<String, Cart> = mutableMapOf()

fun UserSession.addToCart(itemID: Int) = carts.getOrPut(name) { mutableMapOf() }
    .apply { this[itemID] = (this[itemID] ?: 0) + 1 }

fun UserSession.clearCart() = carts.remove(name)

fun UserSession.getCartSize(): Int = carts.getOrPut(name) { mutableMapOf() }.map { it.value }.sum()