package com.chargerfuel

object RestaurantStorage {
    private val restaurants: MutableMap<Int, Menu> = mutableMapOf()

    init {
        restaurants.putAll(SQLUtils.getRestaurantMenus())
    }

    fun getMenu(id: Int): Menu? = restaurants[id]
}