package com.chargerfuel

import com.chargerfuel.pages.*
import com.chargerfuel.pages.Webpage.Companion.load
import io.kvision.*
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher

@Suppress("unused")
val AppScope = CoroutineScope(window.asCoroutineDispatcher())

class App : Application() {
    override fun start(state: Map<String, Any>) {
        //Register pages
        load(LoginPage)
        load(SignupPage)
        load(RestaurantPage)
        load(ForgotPasswordPage)
        load(AccountSettings)
        load(ResetPasswordPage)
        load(CartPage)
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
