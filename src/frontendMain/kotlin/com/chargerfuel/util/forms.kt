package com.chargerfuel.util

import com.chargerfuel.*
import io.kvision.core.*
import io.kvision.form.FormEnctype
import io.kvision.form.FormPanel
import io.kvision.form.FormType
import io.kvision.form.formPanel
import io.kvision.form.text.Password
import io.kvision.form.text.Text
import io.kvision.html.Button
import io.kvision.html.h2
import io.kvision.jquery.invoke
import io.kvision.jquery.jQuery
import io.kvision.utils.perc

inline fun <reified T : Any> Container.basicForm(
    name: String,
    crossinline init: FormPanel<T>.() -> Unit
): FormPanel<T> {
    return formPanel(
        enctype = FormEnctype.MULTIPART,
        type = FormType.INLINE
    ) {
        width = 80.perc
        display = Display.FLEX
        flexDirection = FlexDirection.COLUMN
        justifyContent = JustifyContent.CENTER
        alignContent = AlignContent.CENTER
        gridRowGap = 10
        h2(name)
        init()
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

fun FormPanel<*>.usernameOrEmailBox() {
    add(
        "entry",
        Text(name = "entry") {
            placeholder = "Username/Email"
            width = 100.perc
            input.width = 100.perc
            input.setAttribute("autocapitalize", "none")
        },
        required = true
    )
}

fun FormPanel<*>.phoneNumberBox() {
    add("phone",
        Text(name = "phone") {
            placeholder = "Phone Number"
            width = 100.perc
            input.width = 100.perc
            input.setAttribute("type", "number")
        },
        required = true,
        validatorMessage = { phoneValidationMessage(it.value ?: "") },
        validator = { phoneValidation(it.value ?: "") })
}

fun FormPanel<*>.passwordBox(key: String = "password") {
    add(
        key,
        Password(name = key) {
            placeholder = "Password"
            width = 100.perc
            input.width = 100.perc
            input.setAttribute("autocapitalize", "none")
        },
        required = true
    )
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
    validate(true)
    onEvent { keyup = { validate(true) } }
}

fun FormPanel<*>.submitButton(name: String, post: String, id: String = "button", onSubmit: () -> Unit = {}): Button {
    onEvent {
        keydown = {
            if (it.keyCode == 13) {
                it.preventDefault()
                jQuery("#$id").click()
            }
        }
    }
    return Button(name).apply {
        this.id = id
        alignSelf = AlignItems.END
        onClick {
            if (validate(true)) {
                jQuery("#$id").prop("disabled", true)
                jQuery.post(
                    "/$post",
                    getDataJson(),
                    { data, _, _ ->
                        onSubmit()
                        jQuery("#$id").prop("disabled", false)
                        handleResponse(data.toString())
                    })
            }
        }
    }
}