package com.chargerfuel

import kotlinx.serialization.Serializable

@Serializable
data class LoginInfo(val email: String, val password: String)

@Serializable
data class PasswordResetInfo(val email: String)