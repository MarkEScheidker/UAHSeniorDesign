package com.chargerfuel.util

import kotlinx.browser.window

enum class ScreenType {
    DESKTOP,
    VERTICAL_MOBILE,
    HORIZONTAL_MOBILE
}

fun handleResize(onResize: (ScreenType) -> Unit) {
    onResize(getScreenType())
    window.addEventListener("resize", { onResize(getScreenType()) })
}

fun getScreenType(): ScreenType {
    return if (window.innerHeight >= window.innerWidth) ScreenType.VERTICAL_MOBILE
    else if (window.innerHeight < 600) ScreenType.HORIZONTAL_MOBILE
    else ScreenType.DESKTOP
}