package com.chargerfuel.pages

import io.kvision.Application
import io.kvision.core.*
import io.kvision.html.h1
import io.kvision.html.link
import io.kvision.panel.root
import io.kvision.panel.vPanel
import io.kvision.utils.perc
import io.kvision.utils.px
import io.kvision.utils.vh
import io.kvision.utils.vw

object Main : Webpage {
    override fun create(): Application.() -> Unit = {
        root("main") {
            background = Background(color = Color("DodgerBlue"))
            width = 100.vw; height = 100.vh
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
}