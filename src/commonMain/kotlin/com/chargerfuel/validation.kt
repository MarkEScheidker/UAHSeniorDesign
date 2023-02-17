package com.chargerfuel

val passwordValidation: (String?) -> Boolean = {
    it != null &&
            it.length >= 8
            && it.contains("[A-Z]".toRegex())
            && it.contains("[a-z]".toRegex())
            && it.contains("[0-9]".toRegex())
            && it.contains("[@\$!%*#?&]".toRegex())
}

val passwordValidationMessage: (String?) -> String = {
    if (it == null || it.length < 8) "Password must contain at least 8 characters"
    else if (!it.contains("[A-Z]".toRegex())) "Password must contain an uppercase letter"
    else if (!it.contains("[a-z]".toRegex())) "Password must contain a lowercase letter"
    else if (!it.contains("[0-9]".toRegex())) "Password must contain a number"
    else if (!it.contains("[@\$!%*#?&]".toRegex())) "Password must contain a special character"
    else ""
}

val emailValidation: (String?) -> Boolean = { "[a-z]{2,3}[0-9]{4}@uah.edu".toRegex().matches(it ?: "") }

val emailValidationMessage: (String?) -> String = { "Email must be a valid UAH address" }