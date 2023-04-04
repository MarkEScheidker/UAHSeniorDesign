package com.chargerfuel

object RestaurantState {
    private var restaurants : MutableMap<String,Boolean> = mutableMapOf()

    init{
        restaurants = SQLUtils.getRestaurants().toMutableMap()
    }


}