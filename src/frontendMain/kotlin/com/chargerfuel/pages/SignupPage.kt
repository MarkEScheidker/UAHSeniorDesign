package com.chargerfuel.pages

import com.chargerfuel.LoginInfo
import com.chargerfuel.util.Toolbar.addToolbar
import com.chargerfuel.util.addEmailBox
import com.chargerfuel.util.addPasswordBoxWithConfirmation
import io.kvision.core.*
import io.kvision.form.FormEnctype
import io.kvision.form.FormMethod
import io.kvision.form.FormType
import io.kvision.form.formPanel
import io.kvision.html.ButtonType
import io.kvision.html.button
import io.kvision.html.h1
import io.kvision.panel.Root
import io.kvision.panel.flexPanel
import io.kvision.utils.perc
import io.kvision.utils.px
import io.kvision.utils.vh
import io.kvision.utils.vw
import kotlinx.browser.window

object SignupPage : Webpage("signup") {
    override val html: Root.() -> Unit = {
        addToolbar()
        flexPanel(justify = JustifyContent.CENTER, alignItems = AlignItems.CENTER) {
            width = 100.vw; height = 90.vh
            flexPanel(justify = JustifyContent.CENTER, alignItems = AlignItems.CENTER) {
                width = 80.perc; height = 80.perc
                background = Background(Color.name(Col.ALICEBLUE))
                border = Border(5.px, BorderStyle.SOLID, Color.name(Col.MIDNIGHTBLUE))
                setStyle("border-radius", "25px")
                boxShadow = BoxShadow(0.px, 0.px, 5.px, 5.px, Color.name(Col.MIDNIGHTBLUE))
                formPanel<LoginInfo>(FormMethod.POST, "/signup", FormEnctype.MULTIPART, FormType.INLINE) {
                    width = 80.perc
                    display = Display.FLEX
                    flexDirection = FlexDirection.COLUMN
                    justifyContent = JustifyContent.CENTER
                    alignContent = AlignContent.CENTER
                    gridRowGap = 10
                    h1("Create Account")
                    addEmailBox()
                    addPasswordBoxWithConfirmation("Password")
                    val button = button(text = "Create Account", type = ButtonType.SUBMIT) {
                        disabled = true
                        alignSelf = AlignItems.END
                    }
                    window.addEventListener("change", { button.disabled = !validate(true) })
                }
            }
        }
    }
}