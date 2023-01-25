package com.chargerfuel

import com.chargerfuel.pages.Login
import com.chargerfuel.pages.Main
import io.kvision.*
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher

@Suppress("unused")
val AppScope = CoroutineScope(window.asCoroutineDispatcher())

class App : Application() {
    override fun start(state: Map<String, Any>) {
        //Register pages
        Login.create()()
        Main.create()()
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
