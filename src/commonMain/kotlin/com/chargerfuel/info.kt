package com.chargerfuel

import kotlinx.serialization.Serializable

interface ChargerForm

@Serializable
data class LoginInfo(private val entry: String, val password: String) : ChargerForm {
    val username: String =
        if (userValidation(entry)) entry else if (emailValidation(entry)) entry.replace("@uah.edu", "") else ""
}

@Serializable
data class AccountCreationInfo(
    val email: String,
    val phone: String,
    val password: String,
    val passwordConfirmation: String
) : ChargerForm {
    val username = if (emailValidation(email)) email.replace("@uah.edu", "") else ""
}

@Serializable
data class AccountVerifyInfo(val username: String, val password: String, val phone: String) : ChargerForm

@Serializable
data class PasswordForgotInfo(private val entry: String) : ChargerForm {
    val user: String =
        if (userValidation(entry)) entry else if (emailValidation(entry)) entry.replace("@uah.edu", "") else ""
    val email: String = if (userValidation(entry)) "$entry@uah.edu" else if (emailValidation(entry)) entry else ""
}

@Serializable
data class PasswordResetInfo(val password: String, val passwordConfirmation: String) : ChargerForm

@Serializable
data class PasswordChangeInfo(val oldPassword: String, val password: String, val passwordConfirmation: String) :
    ChargerForm

@Serializable
data class PhoneChangeInfo(val phone: String) : ChargerForm