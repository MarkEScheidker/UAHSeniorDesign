package com.chargerfuel

import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordInfo(val email: String)