package com.chargerfuel.pages

import com.chargerfuel.util.base
import com.chargerfuel.util.center
import com.chargerfuel.util.toolbar
import io.kvision.core.*
import io.kvision.html.Align
import io.kvision.html.h1
import io.kvision.html.image
import io.kvision.panel.Root
import io.kvision.panel.gridPanel
import io.kvision.panel.vPanel
import io.kvision.utils.perc
import io.kvision.utils.px

object Restaurants : Webpage("main") {
    override val html: Root.() -> Unit = {
        // background = Background(color =
        toolbar()
        base {
            vPanel(alignItems = AlignItems.CENTER) {
                center()
                io.kvision.require("css/scrollbars.css")
                position = Position.ABSOLUTE
                colorName = Col.BLACK
                width = 90.perc
                height = 90.perc
                overflowX = Overflow.HIDDEN
                overflowY = Overflow.SCROLL
                alignItems = AlignItems.STRETCH
                padding = 5.perc
                h1(content = "Restaurants") {
                    align = Align.CENTER
                }
                gridPanel(justifyItems = JustifyItems.CENTER) {
                    //addBsBgColor(BsBgColor.WHITE)
                    add(image(io.kvision.require("img/the_den.png") as? String, "The Den") {
                        width = 90.perc
                        height = 75.perc
                    }, 1, 1)
                    add(image(io.kvision.require("img/burrito_bowl.png") as? String, "Burrito Bowl") {
                        width = 90.perc
                        height = 75.perc
                    }, 2, 1)
                    add(image(io.kvision.require("img/boars_head.png") as? String, "Boar's Head") {
                        width = 90.perc
                        height = 80.perc
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
}
