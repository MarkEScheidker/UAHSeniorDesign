package com.chargerfuel

import kotlinx.serialization.Serializable

@Serializable
data class CreateAccountInfo(val email: String, val password: String, val password2: String)