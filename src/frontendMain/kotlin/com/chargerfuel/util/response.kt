package com.chargerfuel.util

import io.kvision.core.AlignItems
import io.kvision.core.Col
import io.kvision.core.Container
import io.kvision.html.div
import io.kvision.jquery.invoke
import io.kvision.jquery.jQuery
import kotlinx.browser.window

fun handleResponse(response: String) {
    when {
        response.startsWith("redirect") -> {
            window.location.href = response.substringAfter("redirect: ")
        }

        response.startsWith("info") -> {
            val split = response.split('|')
            jQuery("#${split[1]}").text(split[2])
        }
    }
}

fun Container.errorBox(id: String = "error") {
    div {
        alignSelf = AlignItems.START
        this.id = id
        colorHex = 0xDC3545
    }
}

fun Container.successBox(id: String = "success") {
    div {
        alignSelf = AlignItems.START
        this.id = id
        colorName = Col.LIMEGREEN
    }
}