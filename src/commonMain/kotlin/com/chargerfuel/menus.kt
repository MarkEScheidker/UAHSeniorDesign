package com.chargerfuel

import kotlinx.serialization.Serializable

@Serializable
data class Item(val name: String, val description: String, val price: Int, val disabled: Boolean)

@Serializable
data class SubMenu(val name: String, val items: Map<Int, Item>)

@Serializable
data class Menu(val name: String, val menus: Map<Int, SubMenu>)