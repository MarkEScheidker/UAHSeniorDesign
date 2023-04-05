package com.chargerfuel.pages
import com.chargerfuel.Order
import com.chargerfuel.util.base
import com.chargerfuel.util.center
import com.chargerfuel.util.handleResponse
import com.chargerfuel.util.restaurantToolbar
import io.kvision.core.*
import io.kvision.html.b
import io.kvision.utils.perc
import io.kvision.html.button
import io.kvision.html.h2
import io.kvision.html.p
import io.kvision.jquery.jQuery
import io.kvision.panel.Root
import io.kvision.panel.VPanel
import io.kvision.panel.hPanel
import io.kvision.panel.vPanel
import io.kvision.utils.px
import kotlinx.browser.window
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

object OrdersPage : Webpage("orders") {
    override val html: Root.() -> Unit = {
        restaurantToolbar()
        base {
            center()
            vPanel {
                center()
                width = 100.perc
                overflowX = Overflow.HIDDEN
                overflowY = Overflow.AUTO
                height = 90.perc
                h2("Orders"){
                    position = Position.RELATIVE
                    left = 50.perc
                    textAlign = TextAlign.CENTER
                    setStyle("transform", "translate(-50%,0%)")
                }
                val menuPanel = vPanel {
                    width = 100.perc
                }
                addAfterInsertHook {
                    jQuery.post("getorders", null, { data, _, _ ->
                        val response = data.toString()
                        console.log(response)
                        if (response.startsWith("[")) updateMenu(Json.decodeFromString(response), menuPanel)
                        else handleResponse(response)
                    })
                    // Refresh the webpage every 0.5 seconds
                    window.setInterval({
                        jQuery.post("getorders", null, { data, _, _ ->
                            val response = data.toString()
                            console.log(response)
                            if (response.startsWith("[")) updateMenu(Json.decodeFromString(response), menuPanel)
                            else handleResponse(response)
                        })
                    }, 2000)
                }
            }
        }
    }
}

private fun updateMenu(list: List<Order>, menuPanel: VPanel) {
    menuPanel.removeAll()
    menuPanel.displayMenu(list)
}

private fun VPanel.displayMenu(list: List<Order>) {

    list.filter { !it.completed }.sortedBy { it.time }.forEach {
        hPanel {
            position = Position.RELATIVE
            left = 50.perc
            setStyle("transform", "translate(-50%,0%)")
            width = 98.perc
            maxWidth = 500.px
            marginBottom = 20.px
            vPanel {
                border = Border(3.px, BorderStyle.SOLID, Color.name(Col.BLACK))
                paddingBottom = 10.px
                width = 70.perc
                b("Order Number: ${it.id}"){
                    textAlign = TextAlign.CENTER
                    marginBottom = 5.px
                }
                it.cart.forEach { (id, pair) ->
                    val (item, count) = pair
                    hPanel {
                        //border = Border(3.px, BorderStyle.SOLID, Color.name(Col.BLACK))
                        b("${item.name}: ") {
                            textAlign = TextAlign.CENTER
                            width = 75.perc
                            maxWidth = 75.perc
                        }
                        b("$count") {
                            textAlign = TextAlign.RIGHT
                            paddingLeft = 20.px
                            top = 50.perc
                        }
                    }
                }
            }
            val id = it.id
            button("Complete") {
                width = 30.perc
                height = 40.px
                onClick {
                    jQuery.post("completeorder", id.toString(), { _, _, _ ->
                        window.location.reload()
                    })
                }
            }
        }
    }
}