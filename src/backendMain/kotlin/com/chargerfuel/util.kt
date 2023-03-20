package com.chargerfuel

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.mindrot.jbcrypt.BCrypt
import kotlin.reflect.full.primaryConstructor

/**
 * Grabs HTML file and converts to String
 * Automatically assumes file exists and will throw an NPE if missing
 *
 * @param file Name of HTML file
 * @return String of entire HTML file
 */
fun getHtml(file: String): String =
    ClassLoader.getSystemResourceAsStream("assets/index.html")!!
        .bufferedReader().use { it.readText().replace("index", file) }

suspend fun ApplicationCall.respondHtml(html: String) = respondText(getHtml(html), ContentType.Text.Html)

suspend inline fun <reified T : ChargerForm> ApplicationCall.construct(): T? {
    return try {
        val parameters = receiveParameters()
        T::class.primaryConstructor?.let {
            val args = it.parameters.map { parameter ->
                parameters[parameter.name ?: throw IllegalArgumentException()] ?: throw NoSuchElementException()
            }.toTypedArray()
            it.call(*args)
        } ?: throw NoSuchElementException()
    } catch (e: Exception) {
        null
    }
}

fun String.encrypt(): String = BCrypt.hashpw(this, BCrypt.gensalt())

fun <T> T.debug(): T = this.also { println(this) }