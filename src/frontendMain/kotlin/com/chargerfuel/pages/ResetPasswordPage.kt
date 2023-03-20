package com.chargerfuel.pages

import com.chargerfuel.PasswordResetInfo
import com.chargerfuel.util.*
import io.kvision.panel.Root

object ResetPasswordPage : Webpage("reset") {
    override val html: Root.() -> Unit = {
        emptyToolbar()
        base {
            basicForm<PasswordResetInfo>("Reset Password") {
                center()
                errorBox()
                passwordBoxWithConfirmation("Password")
                add(submitButton("Reset Password", "reset"))
            }
        }
    }
}