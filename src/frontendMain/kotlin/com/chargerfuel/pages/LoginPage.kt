package com.chargerfuel.pages

import com.chargerfuel.LoginInfo
import com.chargerfuel.util.*
import io.kvision.core.*
import io.kvision.form.text.Password
import io.kvision.html.image
import io.kvision.html.link
import io.kvision.jquery.invoke
import io.kvision.panel.Root
import io.kvision.panel.flexPanel
import io.kvision.panel.hPanel
import io.kvision.panel.vPanel
import io.kvision.require
import io.kvision.utils.perc
import io.kvision.utils.px
import io.kvision.utils.vh
import io.kvision.utils.vw
import kotlinx.browser.window

object LoginPage : Webpage("login") {
    override val html: Root.() -> Unit = {
        //region Background Images
        val slide = Style {
            height = 100.vh
            width = 100.perc
            top = 50.vh
            position = Position.FIXED
            justifySelf = JustifyItems.CENTER
            setStyle("object-fit", "cover")
            setStyle("object-position", "center")
        }
        flexPanel {
            require("css/slideshow.css")
            overflow = Overflow.HIDDEN
            image(require("img/food1.jpg") as? String, "background") {
                addCssStyle(slide)
                setStyle("animation", "slideshow 50s cubic-bezier(.25,.01,.29,.99) 0s infinite")
            }
            image(require("img/food2.jpg") as? String, "background") {
                addCssStyle(slide)
                setStyle("animation", "slideshow 50s cubic-bezier(.25,.01,.29,.99) -10s infinite")
            }
            image(require("img/food3.jpg") as? String, "background") {
                addCssStyle(slide)
                setStyle("animation", "slideshow 50s cubic-bezier(.25,.01,.29,.99) -20s infinite")
            }
            image(require("img/food4.jpg") as? String, "background") {
                addCssStyle(slide)
                setStyle("animation", "slideshow 50s cubic-bezier(.25,.01,.29,.99) -30s infinite")
            }
            image(require("img/food5.jpg") as? String, "background") {
                addCssStyle(slide)
                setStyle("animation", "slideshow 50s cubic-bezier(.25,.01,.29,.99) -40s infinite")
            }
        }
        //endregion
        //region Form Tabs
        flexPanel {
            background = Background(color = Color.rgba(0, 0, 0, 128))
            position = Position.FIXED
            maxWidth = 100.vw; height = 100.vh; right = 0.px; zIndex = 1
            colorName = Col.LIGHTSTEELBLUE
            alignItems = AlignItems.CENTER
            display = Display.FLEX
            flexDirection = FlexDirection.COLUMN
            gridRowGap = 20
            padding = 20.px
            zIndex = 4
            val img = image(require("img/fuel.png") as? String, "banner") { setStyle("object-fit", "contain") }
            basicForm<LoginInfo>("Login") {
                errorBox()
                usernameBox()
                add(
                    "password",
                    Password(name = "password") {
                        placeholder = "Password"
                        width = 100.perc
                        input.width = 100.perc
                        input.setAttribute("autocapitalize", "none")
                    }, required = true
                )
                hPanel {
                    width = 100.perc
                    justifyContent = JustifyContent.SPACEBETWEEN
                    vPanel {
                        link("Forgot Password?", "reset") { colorName = Col.LIGHTSTEELBLUE }
                        link("New to Charger Fuel?", "signup") { colorName = Col.LIGHTSTEELBLUE }
                    }
                    add(submitButton("Login", "login"))
                }

            }
            val resize = {
                if (window.innerHeight >= window.innerWidth) {
                    //Vertical Mobile
                    img.height = 30.vh
                    img.maxWidth = 100.perc
                    width = 100.vw
                    flexDirection = FlexDirection.COLUMN
                } else if (window.innerHeight < 600) {
                    //Horizontal Mobile
                    img.height = 60.vh
                    img.maxWidth = 50.perc
                    width = 100.vw
                    flexDirection = FlexDirection.ROW
                } else {
                    //Desktop
                    img.height = 30.vh
                    img.maxWidth = 100.perc
                    width = 500.px
                    flexDirection = FlexDirection.COLUMN
                }
            }
            resize()
            window.addEventListener("resize", { resize() })
        }
        //endregion
    }
}