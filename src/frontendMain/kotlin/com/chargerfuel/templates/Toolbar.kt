package com.chargerfuel.templates

import io.kvision.core.*
import io.kvision.html.image
import io.kvision.html.link
import io.kvision.panel.hPanel
import io.kvision.require
import io.kvision.utils.px
import io.kvision.utils.vh
import io.kvision.utils.vw

object Toolbar {
    fun Container.addToolbar() {
        hPanel(justify = JustifyContent.SPACEBETWEEN, alignItems = AlignItems.CENTER) {
            width = 100.vw; height = 10.vh
            background = Background(Color.name(Col.ALICEBLUE))
            borderBottom = Border(5.px, BorderStyle.SOLID, Color.name(Col.MIDNIGHTBLUE))
            boxShadow = BoxShadow(0.px, 0.px, 5.px, 5.px, Color.name(Col.MIDNIGHTBLUE))
            hPanel(justify = JustifyContent.START, alignItems = AlignItems.CENTER) {
                paddingLeft = 2.vh
                gridColumnGap = 20
                link("Link1", "/") {
                    fontSize = 3.vh
                    textDecoration = TextDecoration(TextDecorationLine.NONE)
                    colorName = Col.BLACK
                }
                link("Link2", "/") {
                    fontSize = 3.vh
                    textDecoration = TextDecoration(TextDecorationLine.NONE)
                    colorName = Col.BLACK
                }
                link("Link3", "/") {
                    fontSize = 3.vh
                    textDecoration = TextDecoration(TextDecorationLine.NONE)
                    colorName = Col.BLACK
                }
            }
            image(require("img/ico.png") as? String, "icon") {
                maxHeight = 8.vh
                paddingRight = 2.vh
                setStyle("object-fit", "contain")
            }
        }
    }
}