package com.chargerfuel.pages

import com.chargerfuel.LoginInfo
import com.chargerfuel.util.*
import io.kvision.panel.Root

object SignupPage : Webpage("signup") {
    override val html: Root.() -> Unit = {
        toolbar()
        centeredBox {
            basicForm<LoginInfo>("Create Account", "signup") {
                emailBox()
                passwordBoxWithConfirmation("Password")
            }
        }
    }
}