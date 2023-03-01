package com.chargerfuel.pages

import com.chargerfuel.util.base
import com.chargerfuel.util.center
import com.chargerfuel.util.handleResponse
import com.chargerfuel.util.toolbar
import io.kvision.core.AlignItems
import io.kvision.core.Display
import io.kvision.core.Overflow
import io.kvision.form.text.password
import io.kvision.form.text.text
import io.kvision.html.ButtonStyle
import io.kvision.html.button
import io.kvision.html.h1
import io.kvision.html.h3
import io.kvision.jquery.invoke
import io.kvision.jquery.jQuery
import io.kvision.panel.Root
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
                alignItems = AlignItems.STRETCH
                padding = 5.perc
                setStyle("row-gap", "5vh")
                h1("Account Settings") { alignSelf = AlignItems.CENTER }
                vPanel {
                    h3("Username")
                    text {
                        input.apply { id = "username" }
                        disabled = true
                    }
                }
                vPanel {
                    h3("Email")
                    text {
                        input.apply { id = "email" }
                        disabled = true
                    }
                }
                val password = vPanel {
                    h3("Password")
                    text(value = "********") { disabled = true }
                }
                val changePassword = vPanel {
                    display = Display.NONE
                    h3("Password")
                    password { placeholder = "Old Password" }
                    password { placeholder = "New Password" }
                    password { placeholder = "Confirm New Password" }
                }
                password.add(button("Change Password?", style = ButtonStyle.OUTLINEPRIMARY) {
                    alignSelf = AlignItems.END
                    onClick {
                        password.display = Display.NONE
                        changePassword.display = Display.FLEX
                    }
                })
                changePassword.add(button("Change Password", style = ButtonStyle.OUTLINEPRIMARY) {
                    alignSelf = AlignItems.END
                    onClick {
                        changePassword.display = Display.NONE
                        password.display = Display.FLEX
                    }
                })
                vPanel {
                    h3("Payment")
                    text(value = "[PAYMENT TYPE]") { disabled = true }
                    button("Payment Options", style = ButtonStyle.OUTLINEPRIMARY)
                }
                addAfterInsertHook {
                    jQuery.post("/getemail", null, { data, _, _ ->
                        val email = data.toString()
                        jQuery("#username").attr("value", email.substringBefore("@"))
                        jQuery("#email").attr("value", email)
                    })
                }
            }
        }
    }
}