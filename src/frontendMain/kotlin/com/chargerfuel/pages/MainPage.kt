package com.chargerfuel.pages

import com.chargerfuel.util.centeredBox
import com.chargerfuel.util.toolbar
import io.kvision.core.*
import io.kvision.html.h1
import io.kvision.html.link
import io.kvision.panel.Root
import io.kvision.panel.vPanel
import io.kvision.utils.perc
import io.kvision.utils.px

object MainPage : Webpage("main") {
    override val html: Root.() -> Unit = {
        toolbar()
        centeredBox {

        }
    }
}