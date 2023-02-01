package com.chargerfuel

import com.chargerfuel.pages.Login
import com.chargerfuel.pages.Main
import com.chargerfuel.pages.Signup
import io.kvision.*
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher

@Suppress("unused")
val AppScope = CoroutineScope(window.asCoroutineDispatcher())

class App : Application() {
    override fun start(state: Map<String, Any>) {
        //Register pages
        Login.load(this)
        Main.load(this)
        Signup.load(this)
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
