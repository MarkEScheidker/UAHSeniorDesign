package com.chargerfuel.pages

import com.chargerfuel.LoginInfo
import io.kvision.core.*
import io.kvision.form.FormEnctype
import io.kvision.form.FormMethod
import io.kvision.form.formPanel
import io.kvision.form.text.Password
import io.kvision.form.text.Text
import io.kvision.html.*
import io.kvision.panel.Root
import io.kvision.panel.hPanel
import io.kvision.panel.vPanel
import io.kvision.utils.perc
import io.kvision.utils.px
import io.kvision.utils.vh
import io.kvision.utils.vw

object Login : Webpage("login") {
    override val html: Root.() -> Unit = {
        vPanel {
            background = Background(color = Color.rgba(0, 0, 0, 128))
            position = Position.ABSOLUTE
            width = 400.px; maxWidth = 100.vw; height = 100.vh; right = 0.px
            colorName = Col.LIGHTSTEELBLUE
            formPanel(FormMethod.POST, "/login", FormEnctype.MULTIPART) {
                padding = 10.perc
                h1(content = "Login", align = Align.CENTER)
                add(LoginInfo::username, Text(label = "Username", name = "username"))
                add(LoginInfo::password, Password(label = "Password", name = "password"))
                hPanel(justify = JustifyContent.SPACEBETWEEN) {
                    link(label = "Forgot Username/Password", url = "reset")
                    button(text = "Login", type = ButtonType.SUBMIT)
                }
            }
        }
    }
}