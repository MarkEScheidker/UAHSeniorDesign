package com.chargerfuel

object TokenStorage {
    private const val TIMEOUT = 30 * 60 * 1000 //30 minutes in ms

    private data class TokenData(val timestamp: Long, val token: String)

    private var passwordResetTokens: MutableMap<String, TokenData> = mutableMapOf()
    private var accountCreationTokens: MutableMap<String, TokenData> = mutableMapOf()

    fun storePWToken(username: String, token: String) {
        passwordResetTokens[username] = TokenData(System.currentTimeMillis(), token)
    }

    fun retrievePWToken(username: String): String? {
        passwordResetTokens.values.removeIf { System.currentTimeMillis() - it.timestamp > TIMEOUT }
        return passwordResetTokens.remove(username)?.token
    }

    fun doesPWTokenExist(username: String): Boolean {
        accountCreationTokens.values.removeIf { System.currentTimeMillis() - it.timestamp > TIMEOUT }
        return accountCreationTokens.containsKey(username)
    }

    fun storeAccToken(username: String, token: String) {
        accountCreationTokens[username] = TokenData(System.currentTimeMillis(), token)
    }

    fun retrieveAccToken(username: String): String? {
        accountCreationTokens.values.removeIf { System.currentTimeMillis() - it.timestamp > TIMEOUT }
        return accountCreationTokens.remove(username)?.token
    }

    fun doesAccTokenExist(username: String): Boolean {
        accountCreationTokens.values.removeIf { System.currentTimeMillis() - it.timestamp > TIMEOUT }
        return accountCreationTokens.containsKey(username)
    }
}