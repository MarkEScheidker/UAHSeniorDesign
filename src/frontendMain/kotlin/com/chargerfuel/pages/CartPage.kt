package com.chargerfuel.pages

import com.chargerfuel.Item
import com.chargerfuel.util.base
import com.chargerfuel.util.center
import com.chargerfuel.util.handleResponse
import com.chargerfuel.util.toolbar
import io.kvision.core.*
import io.kvision.html.b
import io.kvision.html.button
import io.kvision.html.div
import io.kvision.jquery.jQuery
import io.kvision.panel.Root
import io.kvision.panel.VPanel
import io.kvision.panel.hPanel
import io.kvision.panel.vPanel
import io.kvision.utils.perc
import io.kvision.utils.px
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

object CartPage : Webpage("cart") {
    override val html: Root.() -> Unit = {
        toolbar()
        base {
            vPanel(alignItems = AlignItems.CENTER) {
                center()
                width = 35.perc
                overflowX = Overflow.HIDDEN
                overflowY = Overflow.SCROLL
                height = 90.perc
                addAfterInsertHook {
                    jQuery.post("/getcart", null, { data, _, _ ->
                        val response = data.toString()
                        if (response.startsWith("{")) fillCartPage(Json.decodeFromString(response))
                        else handleResponse(response)
                    })
                }
            }
        }
    }
}

private fun VPanel.reset() {
    removeAll()
    jQuery.post("/getcart", null, { data, _, _ ->
        val response = data.toString()
        if (response.startsWith("{")) fillCartPage(Json.decodeFromString(response))
        else handleResponse(response)
    })
}

private fun VPanel.fillCartPage(cart: Map<Int, Pair<Item, Int>>) {
    cart.forEach { (id, pair) ->
        val (item, count) = pair
        hPanel {
            width = 100.perc
            div {
                width = 100.perc
                border = Border(3.px, BorderStyle.SOLID, Color.name(Col.BLACK))
                b("${item.name}: $count") {
                    textAlign = TextAlign.CENTER
                    minWidth = 100.perc
                }
            }
            button("Remove From Cart") {
                onClick {
                    jQuery.post("/cartremove", id.toString(), { data, _, _ ->
                        handleResponse(data.toString())
                        reset()
                    })
                }
            }
        }
    }
    button("Place Order") {
        width = 100.perc
        marginTop = 50.perc
        onClick {
            jQuery.post("/placeorder", null, { data, _, _ ->
                handleResponse(data.toString())
            })
        }
    }
}