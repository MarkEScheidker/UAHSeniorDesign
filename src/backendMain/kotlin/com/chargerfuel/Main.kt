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
            challenge("/login")
        }
        session<UserSession>("sesh") {
            validate { it }
            challenge("/login")
        }
    }

    install(Sessions) {
        //TODO Replace hard coded secret keys with cryptographically random equivalents @Gabriel @Asher
        val secretEncryptKey = hex("00112233445566778899aabbccddeeff")
        val secretSignKey = hex("6819b57a326945c1968f45236589")
        cookie<UserSession>("user_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 60 * 30
            transform(SessionTransportTransformerEncrypt(secretEncryptKey, secretSignKey))
        }
    }

    routing {
        getAllServiceManagers().forEach { applyRoutes(it) }
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
        get("/") { call.respondRedirect("/login") }
        get("/index.html") { call.respondRedirect("/login") }
        get("/login") {
            call.sessions.get<UserSession>()
                ?.let { call.respondRedirect("/main") }
                ?: call.respondText(getHtml("login"), ContentType.Text.Html)
        }
        intercept(ApplicationCallPipeline.Fallback) { call.respondRedirect("/login") }
    }
    val module = module {
        factoryOf(::PingService)
    }
    kvisionInit(module)
}
