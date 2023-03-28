package com.chargerfuel

object RestaurantStorage {
    private val restaurants: MutableMap<Int, Menu> = mutableMapOf()

    init {
        restaurants.putAll(SQLUtils.getRestaurantMenus())
    }

    fun getMenu(id: Int): Menu? = restaurants[id]

    fun getItem(id: Int): Item? =
        restaurants.values
            .flatMap { it.menus.values }
            .map { it.items }
            .find { it.containsKey(id) }
            ?.get(id)
}