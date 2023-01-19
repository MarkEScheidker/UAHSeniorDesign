package com.chargerfuel

import kotlinx.serialization.Serializable

@Serializable
data class LoginInfo(val username: String, val password: String)
