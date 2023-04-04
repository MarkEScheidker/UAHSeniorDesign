package com.chargerfuel.pages

import com.chargerfuel.PasswordChangeInfo
import com.chargerfuel.PhoneChangeInfo
import com.chargerfuel.util.*
import io.kvision.core.AlignItems
import io.kvision.core.JustifyContent
import io.kvision.core.Overflow
import io.kvision.form.text.text
import io.kvision.html.*
import io.kvision.jquery.invoke
import io.kvision.jquery.jQuery
import io.kvision.panel.Root
import io.kvision.panel.hPanel
import io.kvision.panel.stackPanel
import io.kvision.panel.vPanel
import io.kvision.utils.perc

object RestaurantViewPage: Webpage("resmain") {
    override val html: Root.() -> Unit = {
        restaurantToolbar()
        base {
            vPanel {
                center()
                io.kvision.require("css/scrollbars.css")
                width = 80.perc
                height = 90.perc
                overflowX = Overflow.HIDDEN
                overflowY = Overflow.SCROLL
                alignItems = AlignItems.CENTER
                padding = 5.perc
                setStyle("row-gap", "5vh")
                h1("Restaurant Management") { alignSelf = AlignItems.CENTER }
                vPanel{
                    width = 80.perc
                    button("") {
                        this.id = "openorclosed"
                        onClick {
                            jQuery.post(
                                "/toggleopenclosed",
                                { data, _, _ -> handleResponse(data.toString()) })
                        }
                    }
                    jQuery.post(
                        "/restaurantstate",
                        { data, _, _ -> handleResponse(data.toString()) })

                }
                vPanel {
                    width = 80.perc
                    h3("Username")
                    text {
                        input.apply { id = "username" }
                        disabled = true
                    }
                }
                stackPanel(false) {
                    width = 80.perc; height = 100.perc
                    vPanel {
                        h3("Password")
                        errorBox("passF")
                        successBox("passS")
                        text(value = "********") { disabled = true }
                        button("Change Password?", style = ButtonStyle.OUTLINEPRIMARY) {
                            alignSelf = AlignItems.END
                            onClick { activeIndex = 1 }
                        }
                    }
                    basicForm<PasswordChangeInfo>("Change Password") {
                        width = 100.perc
                        passwordBox("oldPassword")
                        passwordBoxWithConfirmation("New Password")
                        hPanel {
                            width = 100.perc
                            justifyContent = JustifyContent.SPACEBETWEEN
                            button("Go Back") {
                                onClick {
                                    clearData()
                                    activeIndex = 0
                                }
                            }
                            add(submitButton("Change Password", "reschangepassword") {
                                clearData()
                                activeIndex = 0
                            })
                        }
                    }
                }
                addAfterInsertHook {
                    jQuery.post("/getemail", null, { data, _, _ ->
                        val username = data.toString()
                        jQuery("#username").attr("value", username)
                        jQuery("#email").attr("value", "$username@uah.edu")
                    })
                }
            }
        }
    }
}