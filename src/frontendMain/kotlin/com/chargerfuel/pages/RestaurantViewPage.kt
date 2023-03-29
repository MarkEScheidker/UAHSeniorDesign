package com.chargerfuel.pages

import com.chargerfuel.util.base
import com.chargerfuel.util.restaurantToolbar
import io.kvision.html.p
import io.kvision.panel.Root

object RestaurantViewPage: Webpage("resmain") {
    override val html: Root.() -> Unit = {
        restaurantToolbar()
        base {
            p("Restaurant Login :)")
        }
    }
}