package com.chargerfuel

import kotlinx.serialization.Serializable

interface ChargerForm

@Serializable
data class LoginInfo(private val username: String, val password: String) : ChargerForm {
    val user: String = if (username.contains('@')) username.substringBefore('@') else username
}

@Serializable
data class AccountCreationInfo(
    val email: String,
    val phone: String,
    val password: String,
    val passwordConfirmation: String
) : ChargerForm {
    val username = email.replace("@uah.edu", "")
}

@Serializable
data class AccountVerifyInfo(val username: String, val password: String, val phone: String) : ChargerForm

@Serializable
data class PasswordForgotInfo(private val username: String) : ChargerForm {
    val user: String = if (username.contains('@')) username.substringBefore('@') else username
    val email: String = if (username.contains('@')) username else "$username@uah.edu"
}

@Serializable
data class PasswordResetInfo(val password: String, val passwordConfirmation: String) : ChargerForm

@Serializable
data class PasswordChangeInfo(val oldPassword: String, val password: String, val passwordConfirmation: String) :
    ChargerForm

@Serializable
data class PhoneChangeInfo(val phone: String) : ChargerForm