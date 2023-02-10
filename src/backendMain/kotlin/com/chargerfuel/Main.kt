package com.chargerfuel

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

    install(Authentication) {
        form("login") {
            userParamName = "username"
            passwordParamName = "password"

            validate { if (it.name == "admin" && it.password == "password") UserIdPrincipal(it.name) else null }

            //TODO replace validate above with validate below once SQLUtils.getHashedPW() works
            /*
            validate {
                if (Security.comparePasswords(it.password,SQLUtils.getHashedPW(it.name))) {
                    UserIdPrincipal(it.name)
                } else {
                    null
                }
            }
            */

            challenge("/login")
        }
        session<UserSession>("sesh") {
            validate { it }
            challenge("/login")
        }
    }

    install(Sessions) {
        val secretEncryptKey = Security.generateXByteKey(16)
        val secretSignKey = Security.generateXByteKey(32)
        cookie<UserSession>("user_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 60 * 30
            transform(SessionTransportTransformerEncrypt(secretEncryptKey, secretSignKey))
        }
    }

    fun Routing.nonAuthRoute(nonAuth: String, fail: String) = get("/$nonAuth") {
        call.sessions.get<UserSession>()
            ?.let { call.respondRedirect("/$fail") }
            ?: call.respondHtml(nonAuth)
    }

    routing {
        authenticate("login") {
            post("/login") {
                call.sessions.set(UserSession(call.principal<UserIdPrincipal>()?.name.toString()))
                call.respondRedirect("/main")
            }
        }
        authenticate("sesh") {
            get("/main") { call.respondHtml("main") }
            get("/logout") {
                call.sessions.clear<UserSession>()
                call.respondRedirect("/login")
            }
        }
        get("/test") { call.respondHtml("test") }
        nonAuthRoute("login", "main")
        nonAuthRoute("signup", "main")
        nonAuthRoute("reset", "main")
        get("/") { call.respondRedirect("/login") }
        get("/index.html") { call.respondRedirect("/login") }
        intercept(ApplicationCallPipeline.Fallback) { call.respondRedirect("/login") }
    }
    kvisionInit()
}
