package com.chargerfuel.pages

import io.kvision.html.h1
import io.kvision.panel.Root

object ResetPage: Webpage("reset") {
    override val html: Root.() -> Unit = {
        h1("Reset password page")
    }
}