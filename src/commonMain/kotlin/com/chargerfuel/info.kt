package com.chargerfuel

import kotlinx.serialization.Serializable

interface ChargerForm {
    fun verify(): Boolean
}

@Serializable
data class LoginInfo(private val entry: String, val password: String) : ChargerForm {
    val username: String =
        if (userValidation(entry)) entry else if (emailValidation(entry)) entry.replace("@uah.edu", "") else ""

    override fun verify(): Boolean = (emailValidation(entry) || userValidation(entry)) && passwordValidation(password)
}

@Serializable
data class AccountCreationInfo(
    val email: String,
    val phone: String,
    val password: String,
    private val passwordConfirmation: String
) : ChargerForm {
    val username = if (emailValidation(email)) email.replace("@uah.edu", "") else ""
    override fun verify(): Boolean =
        emailValidation(email) &&
                phoneValidation(phone) &&
                passwordValidation(password) &&
                passwordValidation(passwordConfirmation)
}

@Serializable
data class AccountVerifyInfo(val username: String, val hash: String, val phone: String) : ChargerForm {
    override fun verify(): Boolean = userValidation(username) && phoneValidation(phone)
}

@Serializable
data class PasswordForgotInfo(private val entry: String) : ChargerForm {
    val user: String =
        if (userValidation(entry)) entry else if (emailValidation(entry)) entry.replace("@uah.edu", "") else ""
    val email: String = if (userValidation(entry)) "$entry@uah.edu" else if (emailValidation(entry)) entry else ""
    override fun verify(): Boolean = userValidation(entry) || emailValidation(entry)
}

@Serializable
data class PasswordResetInfo(val password: String, private val passwordConfirmation: String) : ChargerForm {
    override fun verify(): Boolean = passwordValidation(password) && passwordValidation(passwordConfirmation)
}

@Serializable
data class PasswordChangeInfo(val oldPassword: String, val password: String, private val passwordConfirmation: String) :
    ChargerForm {
    override fun verify(): Boolean =
        passwordValidation(oldPassword) &&
                passwordValidation(password) &&
                passwordValidation(passwordConfirmation)
}

@Serializable
data class PhoneChangeInfo(val phone: String) : ChargerForm {
    override fun verify(): Boolean = phoneValidation(phone)
}