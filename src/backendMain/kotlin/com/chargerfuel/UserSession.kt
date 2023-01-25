package com.chargerfuel

import io.ktor.server.auth.*

data class UserSession(val name: String) : Principal