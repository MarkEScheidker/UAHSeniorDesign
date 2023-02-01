package com.chargerfuel

import java.security.SecureRandom
import org.mindrot.jbcrypt.BCrypt

object Security {
    private val secureRandom = SecureRandom().apply { setSeed(generateSeed(16)) }

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

    fun generateHashedPassword(plainPassword: String): String {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt())
    }

    fun comparePasswords(plainPassword: String, hashedPassword: String): Boolean {
        return BCrypt.checkpw(plainPassword, hashedPassword)
    }
}