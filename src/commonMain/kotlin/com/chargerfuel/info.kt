package com.chargerfuel

import kotlinx.serialization.Serializable

@Serializable
data class LoginInfo(val email: String, val password: String)

@Serializable
data class RequestPasswordResetInfo(val email: String)

@Serializable
data class PasswordResetInfo(val password: String, val hash: String)