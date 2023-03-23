package com.chargerfuel.pages

import com.chargerfuel.GetRestaurantInfo
import com.chargerfuel.Menu
import com.chargerfuel.util.*
import com.chargerfuel.util.ScreenType.*
import io.kvision.core.*
import io.kvision.html.*
import io.kvision.jquery.jQuery
import io.kvision.panel.Root
import io.kvision.panel.flexPanel
import io.kvision.panel.vPanel
import io.kvision.require
import io.kvision.utils.perc
import io.kvision.utils.px
import kotlinx.browser.window
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.w3c.dom.url.URL

object RestaurantPage : Webpage("main") {
    override val html: Root.() -> Unit = {
        toolbar()
        base {
            vPanel(alignItems = AlignItems.CENTER) {
                center()
                require("css/scrollbars.css")
                position = Position.ABSOLUTE
                colorName = Col.BLACK
                width = 90.perc
                height = 90.perc
                overflowX = Overflow.HIDDEN
                overflowY = Overflow.SCROLL
                alignItems = AlignItems.STRETCH
                padding = 5.perc
                flexPanel(
                    FlexDirection.ROW,
                    FlexWrap.WRAP,
                    JustifyContent.SPACEEVENLY,
                    AlignItems.CENTER,
                    AlignContent.SPACEAROUND
                ) {
                    gridRowGap = 20
                    URL(window.location.href).searchParams.get("res")
                        ?.let { res ->
                            addAfterInsertHook {
                                jQuery.post(
                                    "/getrestaurant",
                                    JSON.parse<Json>(Json.encodeToString(GetRestaurantInfo(res))),
                                    { data, _, _ ->
                                        val response = data.toString()
                                        if (response.startsWith("{")) displayMenu(Json.decodeFromString(response))
                                        else handleResponse(response)
                                    })
                            }
                        }
                        ?: run {
                            fun Container.display(image: Image): Div = div {
                                padding = 2.perc
                                background = Background(Color.name(Col.ALICEBLUE))
                                border = Border(2.perc, BorderStyle.SOLID, Color.name(Col.MIDNIGHTBLUE))
                                boxShadow = BoxShadow(0.px, 0.px, 5.px, 5.px, Color.name(Col.MIDNIGHTBLUE))
                                setStyle("border-radius", "50% 20% / 10% 40%")
                                add(image.apply {
                                    width = 100.perc; height = 100.perc
                                    setStyle("pointer-events", "none")
                                    setStyle("object-fit", "contain")
                                })
                                onClick { window.location.href = "main?res=${image.alt}" }
                            }

                            val images = listOf(
                                display(Image(require("img/the_den.png") as? String, "1")),
                                display(Image(require("img/burrito_bowl.png") as? String, "2")),
                                display(Image(require("img/boars_head.png") as? String, "3")),
                                display(Image(require("img/mein_bowl.png") as? String, "4")),
                                display(Image(require("img/dunkin.png") as? String, "5")),
                                display(Image(require("img/charger_brew.png") as? String, "6"))
                            )
                            handleResize { type ->
                                when (type) {
                                    DESKTOP -> images.forEach { it.width = 30.perc }
                                    VERTICAL_MOBILE -> images.forEach { it.width = 90.perc }
                                    HORIZONTAL_MOBILE -> images.forEach { it.width = 45.perc }
                                }
                            }
                        }
                }
            }
        }
    }
}

private fun Container.displayMenu(menu: Menu) {
    //TODO Put display stuff here @Bailey @Mark
    /**
     * Changes made by Sarah:
     * Replaced basic text `div`s with `p`s (p denotes text blocks in HTML)
     * Replaced header `div`s with `h2`s (h2 has larger text)
     * Replaced `deconstructed for` loops with `forEach` (You probably don't even know what deconstruction is yet, I'd avoid using it if not necessary (forEach is much better in this case))
     * Replaced manual search loops with `find` (Instead of looping through the list and manually checking if each one is the one you're looking for, use `find`, it does it for you)
     * Removed console log at end (Not needed anymore)
     * Removed unnecessary div nesting (if a div is unmarked other than inner divs, it's useless)
     */
    URL(window.location.href).searchParams.get("submenu")?.let { name ->
        menu.menus.values.find { it.name == name }?.let { subMenu ->
            vPanel {
                h2(name)
                subMenu.items.values.forEach { item ->
                    p("${item.name}: \$${item.price / 100}.${item.price % 100}")
                    p(item.description)
                }
            }
        }
    } ?: run {
        vPanel {
            h2(menu.name)
            vPanel(alignItems = AlignItems.CENTER) {
                menu.menus.values.forEach { subMenu ->
                    button(subMenu.name).onClick {
                        window.location.href = window.location.href + "&submenu=${subMenu.name}"
                    }
                }
            }
        }
    }
}

