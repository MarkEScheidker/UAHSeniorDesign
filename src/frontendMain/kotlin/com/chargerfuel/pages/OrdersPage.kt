package com.chargerfuel.pages

import com.chargerfuel.Order
import com.chargerfuel.util.base
import com.chargerfuel.util.center
import com.chargerfuel.util.handleResponse
import com.chargerfuel.util.restaurantToolbar
import io.kvision.html.button
import io.kvision.html.p
import io.kvision.jquery.jQuery
import io.kvision.panel.Root
import io.kvision.panel.VPanel
import io.kvision.panel.vPanel
import kotlinx.browser.window
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

object OrdersPage : Webpage("orders") {
    override val html: Root.() -> Unit = {
        restaurantToolbar()
        base {
            center()
            vPanel {
                addAfterInsertHook {
                    jQuery.post("getorders", null, { data, _, _ ->
                        val response = data.toString()
                        console.log(response)
                        if (response.startsWith("[")) displayMenu(Json.decodeFromString(response))
                        else handleResponse(response)
                    })
                }
            }
        }
    }
}

private fun VPanel.displayMenu(list: List<Order>) {
    list.filter { !it.completed }.sortedBy { it.time }.forEach {
        p("${it.cart}")
        val id = it.id
        button("Compelte") {
            onClick {
                jQuery.post("completeorder",  id.toString(), {_, _, _ ->
                    window.location.reload()
                })
            }
        }
    }
}