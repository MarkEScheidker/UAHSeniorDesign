package com.chargerfuel

import kotlin.reflect.KClass

object TokenStorage {
    private const val TIMEOUT = 30 * 60 * 1000 //30 minutes in ms

    private data class Token<T : ChargerForm>(val value: T) {
        val time = System.currentTimeMillis()
    }

    private val tokens: MutableMap<KClass<*>, MutableMap<String, Token<*>>> = mutableMapOf()

    fun <T : ChargerForm> addToken(hash: String, value: T, `class`: KClass<T>) {
        tokens.getOrPut(`class`) { mutableMapOf() }[hash] = Token(value)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : ChargerForm> removeToken(hash: String, `class`: KClass<T>): T? =
        tokens.getOrPut(`class`) { mutableMapOf() }[hash]?.takeIf { System.currentTimeMillis() - it.time < TIMEOUT }?.value as T?

    inline fun <reified T : ChargerForm> addToken(hash: String, value: T): Unit = addToken(hash, value, T::class)

    inline fun <reified T : ChargerForm> removeToken(hash: String): T? = removeToken(hash, T::class)
}