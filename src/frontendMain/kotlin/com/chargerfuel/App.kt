package com.chargerfuel

import io.kvision.Application
import io.kvision.CoreModule
import io.kvision.BootstrapModule
import io.kvision.BootstrapCssModule
import io.kvision.html.Span
import io.kvision.html.span
import io.kvision.module
import io.kvision.panel.root
import io.kvision.startApplication
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch

val AppScope = CoroutineScope(window.asCoroutineDispatcher())

class App : Application() {

    override fun start(state: Map<String, Any>) {

        //This is converted to html that is seen by the user, everything inside is "the webpage"
        val root = root("kvapp") {
            span("I know where you live :)")
        }

        //launch this block of code on the side on startup, it
        AppScope.launch {
            //create a variable by getting a result from the ping function defined in Model
            val pingResult = Model.ping("Hello world from client!")
            //add a span object to the html root
            root.add(Span(pingResult))
        }
    }
}

fun main() {
    startApplication(
        ::App,
        module.hot,
        BootstrapModule,
        BootstrapCssModule,
        CoreModule
    )
}
