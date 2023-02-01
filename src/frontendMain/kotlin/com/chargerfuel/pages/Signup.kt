package com.chargerfuel.pages

import io.kvision.html.h1
import io.kvision.panel.Root

object Signup: Webpage("signup") {
    override val html: Root.() -> Unit = {
        h1("signup page lol")
    }
}