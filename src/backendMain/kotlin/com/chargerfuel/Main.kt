package com.chargerfuel

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.kvision.remote.applyRoutes
import io.kvision.remote.getServiceManager
import io.kvision.remote.kvisionInit
import kotlinx.coroutines.runBlocking

@Suppress("unused")
fun Application.main() {
    install(Compression)

    install(Authentication) {
        form("login") {
            userParamName = "email"
            passwordParamName = "password"

            validate {
                if (Security.comparePasswords(it.password, SQLUtils.getHashedPW(it.name))) {
                    UserIdPrincipal(it.name)
                } else {
                    null
                }
            }

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

    routing {
        //Account Login
        get("/") { call.respondRedirect("/login") }
        get("/login") {
            call.sessions.get<UserSession>()
                ?.let { call.respondRedirect("/main") }
                ?: call.respondHtml("login")
        }
        authenticate("login") {
            post("/login") {
                call.sessions.set(UserSession(call.principal<UserIdPrincipal>()?.name.toString()))
                call.respondRedirect("/main")
            }
        }
        get("/logout") {
            call.sessions.clear<UserSession>()
            call.respondRedirect("/login")
        }

        //Account Creation
        get("/signup") { call.respondHtml("signup") }
        post("/signup") {
            val parameters = call.receiveParameters()
            val email = parameters["email"] ?: TODO("Tell user error has occurred and to try again")
            val password = parameters["password"] ?: TODO("Tell user error has occurred and to try again")
            if (!SQLUtils.isEmailRegistered(email)) {
                val token = Security.generateSecureToken()
                TokenStorage.setAccountCreationToken(email, token)
                EmailService.sendEmailValidation(email, token)
                //TODO Tell user to check email
            } else TODO("Tell user email is already registered")
        }
        get("/verify") {
            call.request.queryParameters["id"]?.let { TokenStorage.getAccountCreationToken(it) }
                ?.let { /*TODO Verify account*/ }
                ?: call.respondRedirect("/signup")
        }

        //Password Resetting
        post("/forgot") {
            val parameters = call.receiveParameters()
            if (parameters.contains("email"))



            val email = call.receiveParameters()["email"] ?: TODO("Tell user error has occurred and to try again")
            if (SQLUtils.isEmailRegistered(email)) {
                val token = Security.generateSecureToken()
                TokenStorage.setPasswordRequestToken(email, token)
                EmailService.sendPasswordReset(email, token)
                //TODO Tell user to check email
            } else TODO("Tell user email is not registered")
        }
        get("/reset") {
            val token = call.request.queryParameters["id"]
            token?.let { TokenStorage.getPasswordRequestToken(it) }?.let {
                TokenStorage.setPasswordResetToken(it, token)
                call.respondHtml("reset")
            } ?: call.respondRedirect("/forgot")
        }
        post("/reset") {
            val password = call.receiveParameters()["password"] ?: TODO("Tell user error has occurred and to try again")

            SQLUtils.setHashedPW()
        }

        //Logged In Pages
        authenticate("sesh") {
            get("/main") { call.respondHtml("main") }
        }

        //Testing
        get("/test") { call.respondHtml("test") }
    }
    kvisionInit()
}