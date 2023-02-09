package com.chargerfuel

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

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
