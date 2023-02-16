package com.chargerfuel

object TokenStorage {
    private const val TIMEOUT = 30 * 60 * 1000 //30 minutes in ms

    private data class TokenData(val timestamp: Long, val email: String)

    private var passwordRequestTokens: MutableMap<String, TokenData> = mutableMapOf()
    private var passwordResetTokens: MutableMap<String, TokenData> = mutableMapOf()
    private var accountCreationTokens: MutableMap<String, TokenData> = mutableMapOf()

    fun setPasswordRequestToken(email: String, token: String) {
        passwordRequestTokens[token] = TokenData(System.currentTimeMillis(), email)
    }

    fun getPasswordRequestToken(token: String): String? {
        passwordRequestTokens.entries.removeIf { System.currentTimeMillis() - it.value.timestamp > TIMEOUT }
        return passwordRequestTokens.remove(token)?.email
    }

    fun setPasswordResetToken(email: String, token: String) {
        passwordResetTokens[token] = TokenData(System.currentTimeMillis(), email)
    }

    fun getPasswordResetToken(token: String): String? {
        passwordResetTokens.entries.removeIf { System.currentTimeMillis() - it.value.timestamp > TIMEOUT }
        return passwordResetTokens.remove(token)?.email
    }

    fun setAccountCreationToken(email: String, token: String) {
        accountCreationTokens[token] = TokenData(System.currentTimeMillis(), email)
    }

    fun getAccountCreationToken(token: String): String? {
        accountCreationTokens.entries.removeIf { System.currentTimeMillis() - it.value.timestamp > TIMEOUT }
        return accountCreationTokens.remove(token)?.email
    }
}