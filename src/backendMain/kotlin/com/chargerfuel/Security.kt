package com.chargerfuel
import java.security.SecureRandom

object Security {

    private val secureRandom = SecureRandom()

    init {
        secureRandom.setSeed(secureRandom.generateSeed(16))
    }

    fun generate16ByteKey(): ByteArray {
        val key = ByteArray(16)
        secureRandom.nextBytes(key)
        return key
    }

    fun generate14ByteKey(): ByteArray {
        val key = ByteArray(14)
        secureRandom.nextBytes(key)
        return key
    }
}