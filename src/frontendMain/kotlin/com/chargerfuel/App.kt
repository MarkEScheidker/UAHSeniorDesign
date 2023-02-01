package com.chargerfuel

import com.chargerfuel.pages.LoginPage
import com.chargerfuel.pages.MainPage
import com.chargerfuel.pages.ResetPage
import com.chargerfuel.pages.SignupPage
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
