package com.example.project

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
        val root = root("kvapp") {
            span("I know where you live :)")
        }
        AppScope.launch {
//            val pingResult = Model.ping("Hello world from client!")
//            root.add(Span(pingResult))
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
