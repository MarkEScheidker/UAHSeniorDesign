package com.chargerfuel.pages

import com.chargerfuel.util.base
import com.chargerfuel.util.handleResponse
import com.chargerfuel.util.toolbar
import io.kvision.html.div
import io.kvision.jquery.jQuery
import io.kvision.panel.Root

object CartPage : Webpage("cart") {
    override val html: Root.() -> Unit = {
        toolbar()
        base {
            div {
                id = "cart"
                addAfterInsertHook {
                    jQuery.post("/getcartsize", null, { data, _, _ ->
                        handleResponse(data.toString())
                    })
                }
            }
        }
    }
}