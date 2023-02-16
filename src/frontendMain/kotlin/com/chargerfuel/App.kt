package com.chargerfuel

import com.chargerfuel.pages.*
import com.chargerfuel.pages.Webpage.Companion.load
import io.kvision.*
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.*

@Suppress("unused")
val AppScope = CoroutineScope(window.asCoroutineDispatcher())

class App : Application() {
    override fun start(state: Map<String, Any>) {
        //Register pages
        load(LoginPage)
        load(MainPage)
        load(SignupPage)
        load(Restaurants)
        load(ForgotPasswordPage)
        load(ResetPasswordPage)
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
