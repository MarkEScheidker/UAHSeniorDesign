package com.chargerfuel.pages

import com.chargerfuel.PasswordChangeInfo
import com.chargerfuel.PhoneChangeInfo
import com.chargerfuel.util.*
import io.kvision.core.AlignItems
import io.kvision.core.JustifyContent
import io.kvision.core.Overflow
import io.kvision.form.text.text
import io.kvision.html.ButtonStyle
import io.kvision.html.button
import io.kvision.html.h1
import io.kvision.html.h3
import io.kvision.jquery.invoke
import io.kvision.jquery.jQuery
import io.kvision.panel.Root
import io.kvision.panel.hPanel
import io.kvision.panel.stackPanel
import io.kvision.panel.vPanel
import io.kvision.require
import io.kvision.utils.perc

object AccountSettings : Webpage("account") {
    override val html: Root.() -> Unit = {
        toolbar()
        base {
            vPanel {
                center()
                require("css/scrollbars.css")
                width = 80.perc
                height = 90.perc
                overflowX = Overflow.HIDDEN
                overflowY = Overflow.SCROLL
                alignItems = AlignItems.CENTER
                padding = 5.perc
                setStyle("row-gap", "5vh")
                h1("Account Settings") { alignSelf = AlignItems.CENTER }
                vPanel {
                    width = 80.perc
                    h3("Username")
                    text {
                        input.apply { id = "username" }
                        disabled = true
                    }
                }
                vPanel {
                    width = 80.perc
                    h3("Email")
                    text {
                        input.apply { id = "email" }
                        disabled = true
                    }
                }
                stackPanel(false) {
                    width = 80.perc; height = 100.perc
                    vPanel {
                        h3("Phone")
                        errorBox("phoneF")
                        successBox("phoneS")
                        text {
                            addAfterInsertHook {
                                jQuery.post("/getphone", null, { data, _, _ ->
                                    val phone = data.toString()
                                    jQuery("#phone").attr("value", phone)
                                })
                            }
                            input.apply { id = "phone" }
                            disabled = true
                        }
                        button("Change Phone Number?", style = ButtonStyle.OUTLINEPRIMARY) {
                            alignSelf = AlignItems.END
                            onClick { activeIndex = 1 }
                        }
                    }
                    basicForm<PhoneChangeInfo>("Change Phone Number") {
                        width = 100.perc
                        phoneNumberBox()
                        hPanel {
                            width = 100.perc
                            justifyContent = JustifyContent.SPACEBETWEEN
                            button("Go Back") {
                                onClick {
                                    clearData()
                                    activeIndex = 0
                                }
                            }
                            add(submitButton("Change Phone Number", "changephone") {
                                clearData()
                                activeIndex = 0
                            })
                        }
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
                            add(submitButton("Change Password", "changepassword") {
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