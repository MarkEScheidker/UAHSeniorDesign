package com.chargerfuel.pages

import com.chargerfuel.PasswordForgotInfo
import com.chargerfuel.util.*
import io.kvision.panel.Root

object ForgotPasswordPage : Webpage("forgot") {
    override val html: Root.() -> Unit = {
        emptyToolbar()
        base {
            basicForm<PasswordForgotInfo>("Forgot Password") {
                id = "info"
                center()
                errorBox()
                usernameBox()
                add(submitButton("Forgot Password", "forgot"))
            }
        }
    }
}