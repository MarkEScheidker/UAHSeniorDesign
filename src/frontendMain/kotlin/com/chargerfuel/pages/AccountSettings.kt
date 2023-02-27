package com.chargerfuel.pages

import com.chargerfuel.util.base
import com.chargerfuel.util.center
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
                    text(value = "[USERNAME]") { disabled = true }
                }
                vPanel {
                    h3("Email")
                    text(value = "[EMAIL]") { disabled = true }
                }
                val phone = vPanel {
                    h3("Phone Number")
                    text(value = "(555) 555-5555") { disabled = true }
                }
                val changePhone = vPanel {
                    display = Display.NONE
                    h3("Phone Number")
                    text { placeholder = "New Phone Number" }
                }
                phone.add(button("Change Phone Number?", style = ButtonStyle.OUTLINEPRIMARY) {
                    alignSelf = AlignItems.END
                    onClick {
                        phone.display = Display.NONE
                        changePhone.display = Display.FLEX
                    }
                })
                changePhone.add(button("Change Phone Number", style = ButtonStyle.OUTLINEPRIMARY) {
                    alignSelf = AlignItems.END
                    onClick {
                        changePhone.display = Display.NONE
                        phone.display = Display.FLEX
                    }
                })
                val password = vPanel {
                    h3("Password")
                    text(value = "********") { disabled = true }
                }
                val changePassword = vPanel {
                    display = Display.NONE
                    h3("Password")
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
            }
        }
    }
}