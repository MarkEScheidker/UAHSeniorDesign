package com.chargerfuel.pages

import com.chargerfuel.PasswordResetInfo
import com.chargerfuel.util.*
import io.kvision.panel.Root

object ForgotPasswordPage : Webpage("forgot") {
    override val html: Root.() -> Unit = {
        emptyToolbar()
        centeredBox {
            basicForm<PasswordResetInfo>("Forgot Password", "forgot") {
                emailBox()
            }
        }
    }
}