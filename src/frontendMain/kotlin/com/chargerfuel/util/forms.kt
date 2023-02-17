package com.chargerfuel.util

import com.chargerfuel.emailValidation
import com.chargerfuel.emailValidationMessage
import com.chargerfuel.passwordValidation
import com.chargerfuel.passwordValidationMessage
import io.kvision.core.*
import io.kvision.form.*
import io.kvision.form.text.Password
import io.kvision.form.text.Text
import io.kvision.html.ButtonType
import io.kvision.html.button
import io.kvision.html.h1
import io.kvision.utils.perc

inline fun <reified T : Any> Container.basicForm(
    name: String,
    post: String,
    crossinline init: FormPanel<T>.() -> Unit
) {
    formPanel(
        FormMethod.POST,
        "/$post",
        FormEnctype.MULTIPART,
        FormType.INLINE
    ) {
        width = 80.perc
        display = Display.FLEX
        flexDirection = FlexDirection.COLUMN
        justifyContent = JustifyContent.CENTER
        alignContent = AlignContent.CENTER
        gridRowGap = 10
        h1(name)
        init()
        val button = button(text = name, type = ButtonType.SUBMIT) {
            alignSelf = AlignItems.END
            disabled = true
        }
        onEvent { change = { button.disabled = !validate(true) } }
    }
}

fun FormPanel<*>.emailBox() {
    add("email",
        Text(name = "email") {
            placeholder = "UAH Email"
            width = 100.perc
            input.width = 100.perc
            input.setAttribute("autocapitalize", "none")
        },
        required = true,
        validatorMessage = { emailValidationMessage(it.value ?: "") },
        validator = { emailValidation(it.value ?: "") })
}

fun FormPanel<*>.passwordBoxWithConfirmation(placeholder: String) {
    val password = Password(name = "password") {
        this.placeholder = placeholder
        width = 100.perc
        input.width = 100.perc
        input.setAttribute("autocapitalize", "none")
    }
    add("password",
        password,
        required = true,
        validatorMessage = { passwordValidationMessage(it.value ?: "") },
        validator = { passwordValidation(it.value ?: "") })
    add("passwordConfirmation",
        Password(name = "passwordConfirmation") {
            this.placeholder = "Confirm $placeholder"
            width = 100.perc
            input.width = 100.perc
            input.setAttribute("autocapitalize", "none")
        },
        required = true,
        validatorMessage = { "Passwords do not match" },
        validator = { password.input.value == it.input.value })
}