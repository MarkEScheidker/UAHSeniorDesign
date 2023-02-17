package com.chargerfuel

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.kvision.remote.kvisionInit
import org.mindrot.jbcrypt.BCrypt
import kotlin.collections.set

private const val TIMEOUT: Long = 1000 * 60 * 30
private val sessionCache: MutableMap<String, Long> = mutableMapOf()

@Suppress("unused")
fun Application.main() {
    install(Compression)

    install(Sessions) {
        val secretEncryptKey = Security.generateXByteKey(16)
        val secretSignKey = Security.generateXByteKey(32)
        cookie<UserSession>("user_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 60 * 30
            transform(SessionTransportTransformerEncrypt(secretEncryptKey, secretSignKey))
        }
    }

    install(Authentication) {
        form("form") {
            userParamName = "email"
            passwordParamName = "password"

            validate {
                if (TokenStorage.removeToken(it.password) == it.name) UserIdPrincipal(it.name)
                else SQLUtils.getHashedPW(it.name)?.let { password ->
                    if (BCrypt.checkpw(it.password, password)) UserIdPrincipal(it.name) else null
                }
            }

            challenge("/login")
        }

        session<UserSession>("session") {
            validate {
                sessionCache[it.name]?.let { time ->
                    if (System.currentTimeMillis() - time > TIMEOUT) {
                        sessionCache.remove(it.name)
                        null
                    } else it
                }
            }
            challenge("/login")
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
        authenticate("form") {
            post("/login") {
                val session = UserSession(call.principal<UserIdPrincipal>()?.name.toString())
                sessionCache[session.name] = System.currentTimeMillis()
                call.sessions.set(session)
                call.respondRedirect("/main")
            }
        }
        get("/logout") {
            call.sessions.get<UserSession>()?.let { sessionCache.remove(it.name) }
            call.sessions.clear<UserSession>()
            call.respondRedirect("/login")
        }

        //Logged In Pages
        authenticate("session") {
            get("/main") { call.respondHtml("main") }
        }

        //Account Creation
        get("/signup") { call.respondHtml("signup") }
        post("/signup") {
            val parameters = call.receiveParameters()
            val email = parameters["email"].takeIf(emailValidation)
                ?: TODO("Tell user error has occurred and to try again")
            if (!SQLUtils.isEmailRegistered(email)) {
                val hash = BCrypt.hashpw(
                    parameters["password"].takeIf(passwordValidation)
                        ?: TODO("Tell user error has occurred and to try again"),
                    BCrypt.gensalt()
                )
                val token = Security.generateSecureToken()
                TokenStorage.addToken(token, "$email:$hash")
                EmailService.sendEmailValidation(email, token)
                //TODO Tell user to check email
                call.respondRedirect("/login")
            } else {
                //TODO Tell user email is already registered"
                call.respondRedirect("/login")
            }
        }
        get("/verify") {
            call.request.queryParameters["id"]
                ?.let { TokenStorage.removeToken(it) }
                ?.let {
                    SQLUtils.addUserAccount(it.substringBefore(':'), it.substringAfter(':'))
                    call.respondRedirect("/login")
                }
                ?: call.respondRedirect("/signup")
        }

        //Password Resetting
        get("/forgot") { call.respondHtml("forgot") }
        post("/forgot") {
            val email = call.receiveParameters()["email"].takeIf(emailValidation)
                ?: TODO("Tell user error has occurred and to try again")
            if (SQLUtils.isEmailRegistered(email)) {
                val token = Security.generateSecureToken()
                TokenStorage.addToken(token, email)
                EmailService.sendPasswordReset(email, token)
                //TODO Tell user to check email
                call.respondRedirect("/login")
            } else {
                //TODO Tell user email is not registered"
                call.respondRedirect("/login")
            }
        }

        //Testing
        get("/test") { call.respondHtml("test") }
    }
    kvisionInit()
}