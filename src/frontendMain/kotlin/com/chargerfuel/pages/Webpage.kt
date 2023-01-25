package com.chargerfuel.pages

import io.kvision.Application
import io.kvision.core.Background
import io.kvision.core.Color
import io.kvision.panel.Root
import io.kvision.panel.root
import io.kvision.utils.vh
import io.kvision.utils.vw

abstract class Webpage(private val name: String) {
    protected abstract val html: Root.() -> Unit

    fun load(app: Application) = app.run {
        root(name) {
            background = Background(color = Color("DodgerBlue"))
            width = 100.vw; height = 100.vh
            html()
        }
    }
}