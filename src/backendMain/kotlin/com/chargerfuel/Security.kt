package com.chargerfuel

import java.security.SecureRandom
import java.util.*

object Security {
    private val secureRandom = SecureRandom().apply {
        setSeed(generateSeed(16))
    }

    fun generateXByteKey(size: Int): ByteArray {
        val key = ByteArray(size)
        secureRandom.nextBytes(key)
        return key
    }

    fun generateSecureToken(): String {
        val bytes = ByteArray(33)
        secureRandom.nextBytes(bytes)
        return Base64.getUrlEncoder().encodeToString(bytes)
    }
}