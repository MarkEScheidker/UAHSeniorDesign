package com.chargerfuel

import io.kvision.core.Background
import io.kvision.core.Color
import io.kvision.core.Overflow
import io.kvision.core.style
import io.kvision.utils.px

object Styles {
    val bodyStyle = style("body") {
        margin = 0.px
        padding = 0.px
        background = Background(color = Color.hex(0x4285F4))
        overflow = Overflow.HIDDEN
    }
}