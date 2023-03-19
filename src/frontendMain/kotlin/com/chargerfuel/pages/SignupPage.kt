package com.chargerfuel.pages

import com.chargerfuel.LoginInfo
import com.chargerfuel.util.*
import io.kvision.panel.Root

object SignupPage : Webpage("signup") {
    override val html: Root.() -> Unit = {
        emptyToolbar()
        base {
            basicForm<LoginInfo>("Create Account", "signup") {
                center()
                emailBox()
                PhoneNumberBox()
                passwordBoxWithConfirmation("Password")
            }
        }
    }
}