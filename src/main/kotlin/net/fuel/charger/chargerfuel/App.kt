package net.fuel.charger.chargerfuel

import io.kvision.Application
import io.kvision.CoreModule
import io.kvision.BootstrapModule
import io.kvision.BootstrapCssModule
import io.kvision.html.div
import io.kvision.html.span
import io.kvision.module
import io.kvision.panel.root
import io.kvision.startApplication

class App : Application() {
    override fun start(state: Map<String, Any>) {
        root("kvapp") {
            span("This is a test of the thing")
        }
    }

    override fun dispose(): Map<String, Any> {
        return mapOf()
    }
}

fun main() {
    startApplication(::App, module.hot, CoreModule)
}
