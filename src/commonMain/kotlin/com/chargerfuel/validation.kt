package com.chargerfuel

val passwordValidation: (String?) -> Boolean = {
    it != null &&
            it.length >= 8
            && it.length <= 32
            && it.contains("[A-Z]".toRegex())
            && it.contains("[a-z]".toRegex())
            && it.contains("[0-9]".toRegex())
            && it.contains("[@\$!%*#?&]".toRegex())
            && !it.contains("[^A-Za-z0-9@\$!%*#?&]".toRegex())
}

val passwordValidationMessage: (String?) -> String = {
    if (it == null || it.length < 8) "Password too short"
    else if (it.length > 32) "Password too long"
    else if (!it.contains("[A-Z]".toRegex())) "Password must contain an uppercase letter"
    else if (!it.contains("[a-z]".toRegex())) "Password must contain a lowercase letter"
    else if (!it.contains("[0-9]".toRegex())) "Password must contain a number"
    else if (!it.contains("[@\$!%*#?&]".toRegex())) "Password must contain a special character (Any of: @$!%*#?&)"
    else if (it.contains("[^A-Za-z0-9@\$!%*#?&]".toRegex())) "Password contains invalid characters"
    else ""
}

val emailValidation: (String?) -> Boolean = { "[a-zA-Z0-9.]+@uah.edu".toRegex().matches(it ?: "") }

val emailValidationMessage: (String?) -> String = { "Email must be a valid UAH address" }

val phoneValidation: (String?) -> Boolean =
    { "^[+]?[(]?[0-9]{3}[)]?[-\\s.]?[0-9]{3}[-\\s.]?[0-9]{4,6}\$".toRegex().matches(it ?: "") }

val phoneValidationMessage: (String?) -> String = { "Phone number is not valid" }