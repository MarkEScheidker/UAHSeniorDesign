package com.chargerfuel.util

import com.chargerfuel.AppScope
import io.kvision.form.FormPanel
import io.kvision.form.text.Text
import io.kvision.utils.perc
import kotlinx.browser.window
import kotlinx.coroutines.launch

fun FormPanel<*>.addEmailBox() {
    add("email",
        Text(name = "email") {
            placeholder = "UAH Email"
            width = 100.perc
            input.width = 100.perc
        },
        required = true,
        validatorMessage =  { "Not a valid UAH email address" },
        validator = { "[a-z]{2,3}[0-9]{4}@uah.edu".toRegex().matches(it.value ?: "") })
}

fun FormPanel<*>.addPasswordBox() {
    add("password",
        Text(name = "password") {
            placeholder = "Password"
            width = 100.perc
            input.width = 100.perc
        },
        required = true,
        validatorMessage = { "TODO" },
        validator = { true })
}

fun FormPanel<*>.addPasswordBoxWithConfirmation(placeholder: String) {
    val password = Text(name = "password") {
        this.placeholder = placeholder
        width = 100.perc
        input.width = 100.perc
    }
    add("password",
        password,
        required = true,
        validatorMessage = { "TODO" },
        validator = { true })
    add("passwordConfirmation",
        Text(name = "passwordConfirmation") {
            this.placeholder = "Confirm $placeholder"
            width = 100.perc
            input.width = 100.perc
        },
        required = true,
        validatorMessage = { "Passwords do not match" },
        validator = { password.input.value == it.input.value })
}