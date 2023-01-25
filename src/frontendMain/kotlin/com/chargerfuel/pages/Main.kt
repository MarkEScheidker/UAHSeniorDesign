package com.chargerfuel.pages

import io.kvision.core.*
import io.kvision.html.h1
import io.kvision.html.link
import io.kvision.panel.Root
import io.kvision.panel.vPanel
import io.kvision.utils.perc
import io.kvision.utils.px

object Main : Webpage("main") {
    override val html: Root.() -> Unit = {
        vPanel(alignItems = AlignItems.CENTER) {
            padding = 100.px
            background = Background(color = Color.rgba(0, 0, 0, 128))
            position = Position.ABSOLUTE
            width = 100.perc
            colorName = Col.LIGHTSTEELBLUE
            h1(content = "You have logged in!")
            link(label = "Logout", url = "logout")
        }
    }
}