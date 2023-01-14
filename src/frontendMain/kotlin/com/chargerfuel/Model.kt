package com.chargerfuel

import io.kvision.remote.getService

object Model {

    private val pingService = getService<IPingService>()

    //this function sends a message to the server, and gets a response back
    suspend fun ping(message: String): String {
        return pingService.ping(message)
    }

}
