package com.chargerfuel

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
