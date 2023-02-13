package com.chargerfuel

import com.chargerfuel.pages.*
import io.kvision.*
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher

@Suppress("unused")
val AppScope = CoroutineScope(window.asCoroutineDispatcher())

class App : Application() {
    override fun start(state: Map<String, Any>) {
        //Register pages
        LoginPage.load(this)
        MainPage.load(this)
        SignupPage.load(this)
        ResetPage.load(this)
        Restaurants.load(this)
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
