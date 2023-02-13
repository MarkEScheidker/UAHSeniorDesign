package com.chargerfuel.pages

import com.chargerfuel.CreateAccountInfo
import com.chargerfuel.templates.Toolbar.addToolbar
import io.kvision.core.*
import io.kvision.form.FormEnctype
import io.kvision.form.FormMethod
import io.kvision.form.FormType
import io.kvision.form.formPanel
import io.kvision.form.text.Text
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

object ResetPage : Webpage("reset") {
    override val html: Root.() -> Unit = {
        addToolbar()
        flexPanel(justify = JustifyContent.CENTER, alignItems = AlignItems.CENTER) {
            width = 100.vw; height = 90.vh
            formPanel(FormMethod.POST, "/reset", FormEnctype.MULTIPART, FormType.INLINE) {
                width = 70.vw; height = 70.vh
                display = Display.FLEX
                flexDirection = FlexDirection.COLUMN
                justifyContent = JustifyContent.CENTER
                background = Background(Color.name(Col.ALICEBLUE))
                alignContent = AlignContent.CENTER
                gridRowGap = 10
                border = Border(5.px, BorderStyle.SOLID, Color.name(Col.MIDNIGHTBLUE))
                setStyle("border-radius", "25px")
                boxShadow = BoxShadow(0.px, 0.px, 5.px, 5.px, Color.name(Col.MIDNIGHTBLUE))
                h1("Reset Password")
                add(
                    CreateAccountInfo::email,
                    Text(name = "email") {
                        placeholder = "UAH Email"
                        width = 80.perc
                        input.apply { width = 100.perc }
                    },
                    required = true,
                    validatorMessage = { "Not a valid UAH email address" },
                    validator = { "[a-z]{2,3}[0-9]{4}@uah.edu".toRegex().matches(it.value ?: "") })
                val button = button(text = "Reset Password", type = ButtonType.SUBMIT) {
                    disabled = true
                    alignSelf = AlignItems.END
                }
                validate(true)
                window.addEventListener("change", { button.disabled = !validate(true) })
            }
        }
    }
}