package com.chargerfuel.pages

import io.kvision.Application

interface Webpage {
    fun create(): Application.() -> Unit
}