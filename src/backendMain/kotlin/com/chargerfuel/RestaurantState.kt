package com.chargerfuel

object RestaurantState {
    private var restaurants : MutableMap<String,Boolean> =  SQLUtils.getRestaurants().toMutableMap()

    fun getRestaurantState(email: String): Boolean? {
        return restaurants[email]
    }

    fun toggleRestaurantState(email: String){
        restaurants[email] = restaurants[email] != true
    }
}