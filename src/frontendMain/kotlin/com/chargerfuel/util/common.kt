package com.chargerfuel.util

import io.kvision.core.*
import io.kvision.html.Div
import io.kvision.html.div
import io.kvision.html.image
import io.kvision.html.link
import io.kvision.panel.SimplePanel
import io.kvision.panel.hPanel
import io.kvision.require
import io.kvision.utils.*

fun SimplePanel.center() {
    position = Position.RELATIVE
    left = 50.perc
    top = 50.perc
    setStyle("transform", "translate(-50%,-50%)")
}

fun SimplePanel.base(init: Div.() -> Unit) {
    div {
        width = 100.vw; height = 90.vh
        div {
            center()
            width = 80.perc; height = 80.perc
            background = Background(Color.name(Col.ALICEBLUE))
            border = Border(5.px, BorderStyle.SOLID, Color.name(Col.MIDNIGHTBLUE))
            setStyle("border-radius", "25px")
            boxShadow = BoxShadow(0.px, 0.px, 5.px, 5.px, Color.name(Col.MIDNIGHTBLUE))
            init()
        }
    }
}

fun SimplePanel.emptyToolbar() {
    hPanel(justify = JustifyContent.CENTER, alignItems = AlignItems.CENTER) {
        width = 100.vw; maxWidth = 100.vw; minWidth = 100.vw
        height = 10.vh; maxHeight = 10.vh; minHeight = 10.vh
        background = Background(Color.name(Col.ALICEBLUE))
        borderBottom = Border(5.px, BorderStyle.SOLID, Color.name(Col.MIDNIGHTBLUE))
        boxShadow = BoxShadow(0.px, 0.px, 5.px, 5.px, Color.name(Col.MIDNIGHTBLUE))
        image(require("img/splash-text.png") as? String, "splash") {
            maxHeight = 8.vh
            setStyle("object-fit", "contain")
        }
    }
}

fun SimplePanel.toolbar() {
    hPanel(justify = JustifyContent.SPACEBETWEEN, alignItems = AlignItems.CENTER) {
        width = 100.vw; maxWidth = 100.vw; minWidth = 100.vw
        height = 10.vh; maxHeight = 10.vh; minHeight = 10.vh
        background = Background(Color.name(Col.ALICEBLUE))
        borderBottom = Border(5.px, BorderStyle.SOLID, Color.name(Col.MIDNIGHTBLUE))
        boxShadow = BoxShadow(0.px, 0.px, 5.px, 5.px, Color.name(Col.MIDNIGHTBLUE))
        hPanel(justify = JustifyContent.START, alignItems = AlignItems.CENTER) {
            paddingLeft = 2.vh
            height = 80.perc
            width = 100.perc
            float = PosFloat.LEFT
            setStyle("column-gap", "2vh")
            link("Order", "/main") {
                setStyle("font-size", "min(3vh, 4vw)")
                textDecoration = TextDecoration(TextDecorationLine.NONE)
                colorName = Col.BLACK
            }
            div {
                background = Background(Color.name(Col.BLACK))
                height = 100.perc
                minWidth = 2.px
                setStyle("border-radius", "2px")
            }
            link("Account", "/account") {
                setStyle("font-size", "min(3vh, 4vw)")
                textDecoration = TextDecoration(TextDecorationLine.NONE)
                colorName = Col.BLACK
            }
            div {
                background = Background(Color.name(Col.BLACK))
                height = 100.perc
                minWidth = 2.px
                setStyle("border-radius", "2px")
            }
            link("Cart", "/cart") {
                setStyle("font-size", "min(3vh, 4vw)")
                textDecoration = TextDecoration(TextDecorationLine.NONE)
                colorName = Col.BLACK
            }
            div {
                background = Background(Color.name(Col.BLACK))
                height = 100.perc
                minWidth = 2.px
                setStyle("border-radius", "2px")
            }
            link("Logout", "/logout") {
                setStyle("font-size", "min(3vh, 4vw)")
                textDecoration = TextDecoration(TextDecorationLine.NONE)
                colorName = Col.BLACK
            }
            image(require("img/ico.png") as? String, "icon") {
                maxHeight = 8.vh
                paddingRight = 2.vh
                marginLeft = auto
                minWidth = 0.px
                setStyle("object-fit", "contain")
            }
        }
    }
}