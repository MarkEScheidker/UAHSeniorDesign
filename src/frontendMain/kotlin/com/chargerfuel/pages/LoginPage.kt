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
import io.kvision.require
import io.kvision.utils.perc
import io.kvision.utils.px
import io.kvision.utils.vh
import io.kvision.utils.vw

object LoginPage : Webpage("login") {
    override val html: Root.() -> Unit = {
        require("css/slideshow.css")
        position = Position.RELATIVE
        width = 100.vw
        height = 100.vh
        overflowX = Overflow.HIDDEN
        image(require("img/login.jpg") as? String, "Food Display") {
            position = Position.ABSOLUTE
            height = 100.vh
            left = 0.perc
            top = 0.perc
            setStyle("animation", "slideshowOne 50s cubic-bezier(.25,.01,.29,.99) infinite")
        }
        image(require("img/login.jpg") as? String, "Food Display") {
            position = Position.ABSOLUTE
            height = 100.vh
            left = 0.perc
            top = 0.perc
            setStyle("animation", "slideshowTwo 50s cubic-bezier(.25,.01,.29,.99) infinite")
        }
        vPanel {
            background = Background(color = Color.rgba(0, 0, 0, 128))
            position = Position.ABSOLUTE
            width = 500.px; maxWidth = 100.vw; height = 100.vh; right = 0.px
            colorName = Col.LIGHTSTEELBLUE
            alignItems = AlignItems.CENTER
            image(require("img/fuel.png") as? String, "Charger Fuel") { width = 400.px; marginTop = 20.px }
            formPanel(FormMethod.POST, "/login", FormEnctype.MULTIPART) {
                width = 100.perc; padding = 10.perc; paddingTop = 5.perc
                h1(content = "Login", align = Align.CENTER)
                add(LoginInfo::username, Text(label = "Username", name = "username"))
                add(LoginInfo::password, Password(label = "Password", name = "password"))
                hPanel(justify = JustifyContent.SPACEBETWEEN) {
                    link(label = "Forgot Username/Password", url = "reset")
                    button(text = "Login", type = ButtonType.SUBMIT)
                }
                link(label = "New to Charger Fuel? Sign Up Here!", url = "signup")
            }
        }
    }
}