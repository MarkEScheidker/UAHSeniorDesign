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
import io.kvision.panel.hPanel
import io.kvision.panel.vPanel
import io.kvision.require
import io.kvision.utils.perc
import io.kvision.utils.px
import io.kvision.utils.vw
import kotlinx.browser.window
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.w3c.dom.History
import org.w3c.dom.url.URL

object RestaurantPage : Webpage("main") {
    override val html: Root.() -> Unit = {
        toolbar()
        base {
            vPanel(alignItems = AlignItems.CENTER) {
                URL(window.location.href).searchParams.get("res")
                    ?.let { res ->
                        top = 47.perc
                        height = 90.perc
                    }?: run{
                        top = 50.perc
                        height = 98.perc
                    }
                left = 50.perc
                width = 98.perc

                setStyle("transform", "translate(-50%,-50%)")
                require("css/scrollbars.css")
                position = Position.ABSOLUTE
                colorName = Col.BLACK
                overflowX = Overflow.HIDDEN
                overflowY = Overflow.AUTO
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
                                display(Image(require("img/papa_johns.png") as? String, "3")),
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
        URL(window.location.href).searchParams.get("res")
            ?.let { res ->
            div {
                height = 5.perc
                width = 50.perc
                maxWidth = 300.px
                position = Position.ABSOLUTE
                bottom = 5.5.perc
                left = 50.perc
                setStyle("transform", "translate(-50%,-50%)")
                button("Back") {
                    center()
                    height = 100.perc
                    width = 100.perc
                }.onClick {
                    window.history.back()
                }
            }
        }
    }
}

private fun Container.displayMenu(menu: Menu) {

    URL(window.location.href).searchParams.get("submenu")?.let { name ->
        menu.menus.values.find { it.name == name }?.let { subMenu ->
            vPanel {
                successBox()
                width = 100.perc
                maxWidth = 400.px
                h2(name, align = Align.CENTER)

                subMenu.items.forEach { (id, item) ->
                    hPanel {
                        paddingBottom = 10.px
                        div {
                            border = Border(3.px, BorderStyle.SOLID, Color.name(Col.LIGHTSTEELBLUE))
                            borderRadius = 3.px
                            width = 65.perc
                            p("${item.name}: \$${item.price/100}.${(item.price%100).toString().padStart(2,'0')}") {
                                fontSize = 14.px
                            }
                            p(item.description) {
                                fontSize = 10.px
                            }
                        }
                        vPanel {
                            width = 35.perc
                            button("") {
                                if (item.disabled) {
                                    p("Out Of Stock") {
                                        fontSize = 10.px
                                    }
                                    style = ButtonStyle.OUTLINEDANGER
                                } else {
                                    p("Add To Cart") {
                                        fontSize = 10.px
                                    }
                                    style = ButtonStyle.SUCCESS
                                }
                                disabled = item.disabled
                                height = 30.px
                                maxWidth = 120.px

                            }.onClick {
                                jQuery.post(
                                    "/cartadd",
                                    id.toString(),
                                    { data, _, _ -> handleResponse(data.toString()) })
                            }
                            div {
                                this.id = id.toString()
                                colorName = Col.LIMEGREEN
                                fontSize = 10.px
                                fontSize = 0.80.vw
                                marginLeft = 2.px
                                marginTop = 2.px
                                setStyle("white-space","nowrap")
                            }
                        }
                    }
                }
            }
        }
    } ?: run {
        vPanel(alignItems = AlignItems.CENTER) {
            h2(menu.name)
            vPanel(alignItems = AlignItems.CENTER) {
                menu.menus.values.forEach { subMenu ->
                    button(subMenu.name, style = ButtonStyle.INFO) {
                        width = 100.perc
                        height = 75.perc
                        fontSize = 16.px
                        marginBottom = 10.px
                    }.onClick {
                        window.location.href = window.location.href + "&submenu=${subMenu.name}"
                    }
                }
            }
        }
    }
}