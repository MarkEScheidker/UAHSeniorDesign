package com.chargerfuel

import kotlinx.serialization.Serializable

@Serializable
data class Item(val id: Int, val name: String, val description: String, val price: Int)

@Serializable
data class SubMenu(val id: Int, val name: String, val items: List<Item>)

@Serializable
data class Menu(val id: Int, val name: String, val menus: List<SubMenu>)