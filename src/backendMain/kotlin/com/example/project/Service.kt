package com.example.project

@Suppress("ACTUAL_WITHOUT_EXPECT")

//a class for the ping service
actual class PingService : IPingService {

    // the ping function takes in a message from the client, prints it, then sends a message back
    override suspend fun ping(message: String): String {
        println(message)
        return "Hello world from server!"
    }
}
