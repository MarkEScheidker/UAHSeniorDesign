package com.chargerfuel

import io.ktor.websocket.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.LinkedHashSet

object WebSocketManager {
    private val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())

    fun addConnection(connection: Connection) {
        connections += connection
    }

    fun removeConnection(connection: Connection) {
        connections -= connection
    }

    private suspend fun sendWebSocketMessage(webSocket: WebSocketSession, message: String) {
        withTimeout(5000L) {
            try {
                webSocket.send(message)
            } catch (e: Throwable) {
                println("Error sending message: ${e.message}")
            }
        }
    }
    fun sendMessageOnWebSocket(webSocket: WebSocketSession, message: String) {
        val job = GlobalScope.launch {
            sendWebSocketMessage(webSocket, message)
        }
        job.invokeOnCompletion {
            println("Message sent and coroutine completed.")
            job.cancel()
        }
    }

    fun broadcast(message: String) {
        connections.forEach { connection ->
            sendMessageOnWebSocket(connection.session, message)
        }
    }

    class Connection(val session: DefaultWebSocketSession) {
        companion object {
            val lastId = AtomicInteger(0)
        }
        val name = "user${lastId.getAndIncrement()}"
    }
}