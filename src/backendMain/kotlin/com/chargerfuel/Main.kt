package com.chargerfuel

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import io.kvision.remote.applyRoutes
import io.kvision.remote.getAllServiceManagers
import io.kvision.remote.kvisionInit
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

@Suppress("unused")
fun Application.main() {
    install(Compression)

    //TODO handle database account validation @Gabriel @Brad
    install(Authentication) {
        form("login") {
            userParamName = "username"
            passwordParamName = "password"
            validate { if (it.name == "admin" && it.password == "password") UserIdPrincipal(it.name) else null }
            challenge { call.respondRedirect("/login") }
        }
        session<UserSession>("sesh") {
            validate { it }
            challenge { call.respondRedirect("/login") }
        }
    }

    //TODO handle session encryption @Gabriel @Asher https://ktor.io/docs/sessions.html
    install(Sessions) {
        //non-static keys should be generated for true security, this is a demonstration
        val secretEncryptKey = hex("00112233445566778899aabbccddeeff")
        val secretSignKey = hex("6819b57a326945c1968f45236589")
        cookie<UserSession>("user_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 60 * 30
            cookie.secure = true
            transform(SessionTransportTransformerEncrypt(secretEncryptKey, secretSignKey))
        }
    }

    routing {
        getAllServiceManagers().forEach { applyRoutes(it) }
        get("/") { call.respondRedirect("/login") }
        get("/login") { call.respondText(getHtml("login"), ContentType.Text.Html) }
        authenticate("login") {
            post("/login") {
                call.sessions.set(UserSession(call.principal<UserIdPrincipal>()?.name.toString()))
                call.respondRedirect("/main")
            }
        }
        authenticate("sesh") {
            get("/main") { call.respondText(getHtml("main"), ContentType.Text.Html) }
            get("/logout") {
                call.sessions.clear<UserSession>()
                call.respondRedirect("/login")
            }
        }
    }
    val module = module {
        factoryOf(::PingService)
    }
    kvisionInit(module)
}
