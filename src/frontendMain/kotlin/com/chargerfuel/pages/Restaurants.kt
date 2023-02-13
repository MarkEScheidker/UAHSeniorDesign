package com.chargerfuel.pages

import io.kvision.core.*
import io.kvision.html.h1
import io.kvision.html.image
import io.kvision.panel.Root
import io.kvision.panel.gridPanel
import io.kvision.panel.vPanel
import io.kvision.utils.perc
import io.kvision.utils.px

object Restaurants : Webpage("test") {
    override val html: Root.() -> Unit = {
        // background = Background(color = )
        vPanel(alignItems = AlignItems.CENTER) {
            padding = 100.px
            position = Position.ABSOLUTE
            width = 100.perc
            colorName = Col.BLACK
            h1(content = "Restaurants") {
                addBsBgColor(BsBgColor.WHITE)
            }
            gridPanel (justifyItems = JustifyItems.CENTER){
                addBsBgColor(BsBgColor.WHITE)
                Border(1.px,BorderStyle.SOLID, Color.rgba(0,0,0,1))
                add(image(io.kvision.require("img/the_den.jpg") as? String, "The Den") {
                    width = 90.perc
                    height = 75.perc
                }, 1, 1)
                add(image(io.kvision.require("img/burrito_bowl.jpg") as? String, "Burrito Bowl") {
                    width = 90.perc
                    height = 75.perc
                }, 2, 1)
                add(image(io.kvision.require("img/boars_head.jpg") as? String, "Boar's Head") {
                    width = 90.perc
                    height = 65.perc
                    addBsBgColor(BsBgColor.WHITE)
                }, 1, 2)
                add(image(io.kvision.require("img/mein_bowl.png") as? String, "Mein Bowl") {
                    width = 90.perc
                    height = 65.perc
                }, 2, 2)
                add(image(io.kvision.require("img/dunkin.png") as? String, "Dunkin") {
                    width = 75.perc
                    height = 75.perc
                }, 1, 3)
                add(image(io.kvision.require("img/charger_brew.png") as? String, "Charger Brew") {
                    width = 75.perc
                    height = 75.perc
                }, 2, 3)
            }
        }
    }

}
