package com.chargerfuel

import java.security.SecureRandom
import org.mindrot.jbcrypt.BCrypt

object Security {
    private val secureRandom = SecureRandom().apply { setSeed(generateSeed(16)) }

    fun generateXByteKey(size: Int): ByteArray {
        val key = ByteArray(size)
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