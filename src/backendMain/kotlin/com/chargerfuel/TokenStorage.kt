package com.chargerfuel

object TokenStorage {
    private const val TIMEOUT = 30 * 60 * 1000 //30 minutes in ms

    private data class Token(val value: String) {
        val time = System.currentTimeMillis()
    }

    private val tokens: MutableMap<String, Token> = mutableMapOf()

    private fun sanitize() {
        tokens.entries.removeIf { System.currentTimeMillis() - it.value.time > TIMEOUT }
    }

    fun addToken(token: String, value: String) {
        sanitize()
        tokens[token] = Token(value)
    }

    fun removeToken(token: String): String? {
        sanitize()
        return tokens.remove(token)?.value
    }
}