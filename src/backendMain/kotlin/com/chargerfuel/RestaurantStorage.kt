package com.chargerfuel

object RestaurantStorage {
    private val restaurants: MutableMap<Int, Menu> = mutableMapOf()

    init {
        restaurants.putAll(SQLUtils.getRestaurantMenus())
    }

    fun getMenu(id: Int): Menu? = restaurants[id]

    fun getMenu(name: String): Menu? = when (name) {
        "den" -> 1
        "burrito" -> 2
        "papa" -> 3
        "mein" -> 4
        "dunkin" -> 5
        "brew" -> 6
        else -> null
    }?.let { getMenu(it) }

    fun getItem(id: Int): Item? =
        restaurants.values
            .flatMap { it.menus.values }
            .map { it.items }
            .find { it.containsKey(id) }
            ?.get(id)

    fun getMenu(item: Item): Menu =
        restaurants.values.find {
            it.menus.values.flatMap { it.items.values }.contains(item)
        }!!
}