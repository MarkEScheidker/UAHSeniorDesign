package com.chargerfuel.pages

import com.chargerfuel.util.base
import com.chargerfuel.util.toolbar
import io.kvision.panel.Root

object MainPage : Webpage("main") {
    override val html: Root.() -> Unit = {
        toolbar()
        base {}
    }
}