package com.chargerfuel.util

import io.kvision.jquery.invoke
import io.kvision.jquery.jQuery
import kotlinx.browser.window

fun handleResponse(response: String) {
    when {
        response.startsWith("redirect") -> {
            window.location.href = response.substringAfter("redirect: ")
        }

        response.startsWith("error") -> {
            jQuery("#error").text(response.substringAfter("error: "))
        }
    }
}