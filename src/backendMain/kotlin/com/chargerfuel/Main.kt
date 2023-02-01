package com.chargerfuel

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.kvision.remote.kvisionInit

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
        val secretEncryptKey = generate16ByteKey()
        val secretSignKey = generate14ByteKey()
        cookie<UserSession>("user_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 60 * 30
            transform(SessionTransportTransformerEncrypt(secretEncryptKey, secretSignKey))
        }
    }

    fun Routing.nonAuthRoute(nonAuth: String, fail: String) = get("/$nonAuth") {
        call.sessions.get<UserSession>()
            ?.let { call.respondRedirect("/$fail") }
            ?: call.respondText(getHtml(nonAuth), ContentType.Text.Html)
    }

    routing {
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
        get("/test") { call.respondText(getHtml("test"), ContentType.Text.Html) }
        nonAuthRoute("login", "main")
        nonAuthRoute("signup", "main")
        nonAuthRoute("reset", "main")
        get("/") { call.respondRedirect("/login") }
        get("/index.html") { call.respondRedirect("/login") }
        intercept(ApplicationCallPipeline.Fallback) { call.respondRedirect("/login") }
    }
    kvisionInit()
}
