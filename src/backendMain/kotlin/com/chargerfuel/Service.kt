package com.chargerfuel

//a class for the ping service
@Suppress("ACTUAL_WITHOUT_EXPECT")
actual class PingService : IPingService {

    // the ping function takes in a message from the client, prints it, then sends a message back
    override suspend fun ping(message: String): String {
        return "You are the ${ConnectionCounter.count()} person to connect to this server! :)"
    }
}
