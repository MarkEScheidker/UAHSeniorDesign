package com.chargerfuel.pages

import com.chargerfuel.AccountCreationInfo
import com.chargerfuel.util.*
import io.kvision.core.onEvent
import io.kvision.panel.Root

object SignupPage : Webpage("signup") {
    override val html: Root.() -> Unit = {
        emptyToolbar()
        base {
            basicForm<AccountCreationInfo>("Create Account") {
                id = "info"
                center()
                errorBox()
                emailBox()
                phoneNumberBox()
                passwordBoxWithConfirmation("Password")
                add(submitButton("Create Account", "signup"))
            }
        }
    }
}