 package com.chargerfuel

import io.kvision.*
import io.kvision.core.AlignItems
import io.kvision.form.FormEnctype
import io.kvision.form.FormMethod
import io.kvision.form.formPanel
import io.kvision.form.text.Password
import io.kvision.form.text.Text
import io.kvision.html.Align
import io.kvision.html.ButtonType
import io.kvision.html.button
import io.kvision.html.h1
import io.kvision.panel.root
import io.kvision.panel.vPanel
import io.kvision.utils.perc
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher

val AppScope = CoroutineScope(window.asCoroutineDispatcher())

class App : Application() {

    override fun start(state: Map<String, Any>) {
        Styles

        //This is converted to html that is seen by the user, everything inside is "the webpage"
        val root = root("kvapp") {
            formPanel(FormMethod.POST, "/login", FormEnctype.MULTIPART) {
                padding = 20.perc
                h1(content = "Student Login", align = Align.CENTER)
                add(LoginInfo::username, Text(label = "Username/Email", name = "username"))
                add(LoginInfo::password, Password(label = "Password", name = "password"))
                vPanel(alignItems = AlignItems.END) {
                    button(text = "Login", type = ButtonType.SUBMIT)
                }
            }
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
