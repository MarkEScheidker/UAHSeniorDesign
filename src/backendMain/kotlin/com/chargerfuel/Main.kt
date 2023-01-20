package com.chargerfuel

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.kvision.remote.applyRoutes
import io.kvision.remote.getAllServiceManagers
import io.kvision.remote.kvisionInit
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

fun Application.main() {
    //compress all the packets
    install(Compression)

    install(Authentication) {
        form("login") {
            userParamName = "username"
            passwordParamName = "password"
            //Redirects you back to main page if failed to login TODO replace redirect with "password incorrect" message
            challenge("/")
        }
    }

    routing {
        getAllServiceManagers().forEach { applyRoutes(it) }
        //Handles login authentication (incomplete)
        authenticate("login") {
            post("/login") {}
        }
        //Redirects you to main page if directly connected to charger.food.is/login
        get("/login") {
            call.respondRedirect("/")
        }
    }
    val module = module {
        factoryOf(::PingService)
    }
    kvisionInit(module)
}
