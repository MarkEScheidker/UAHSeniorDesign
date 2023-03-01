package com.chargerfuel

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import io.kvision.remote.kvisionInit
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.mindrot.jbcrypt.BCrypt
import java.time.Duration

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

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
    }

    fun validateLogin(email: String, password: String): UserIdPrincipal? =
        if (TokenStorage.removeToken(password) == email) UserIdPrincipal(email)
        else SQLUtils.getHashedPW(email)?.let {
            if (BCrypt.checkpw(password, it)) UserIdPrincipal(email) else null
        }

    fun ApplicationCall.getSession(): UserSession? =
        sessions.get<UserSession>()?.takeIf { sessionCache.containsKey(it.name) }

    routing {
        //Account Login
        get("/") { call.respondRedirect("/login") }
        get("/login") {
            call.getSession()
                ?.let { call.respondRedirect("/main") }
                ?: call.respondHtml("login")
        }
        post("/login") {
            call.receiveParameters().let {
                val email = it["email"] ?: run {
                    call.respondText("error: An Unknown Error Occurred, Please Try Again")
                    return@post
                }
                val password = it["password"] ?: run {
                    call.respondText("error: An Unknown Error Occurred, Please Try Again")
                    return@post
                }
                println("$email:$password")
                validateLogin(email, password)?.let { principal ->
                    val session = UserSession(principal.name)
                    sessionCache[session.name] = System.currentTimeMillis()
                    call.sessions.set(session)
                    call.respondText("redirect: main")
                } ?: call.respondText("error: Incorrect Username/Password")
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
            get("/account") { call.respondHtml("account") }
            post("/getemail") {
                call.getSession()?.let { call.respondText(it.name) }
            }
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
                    val email = it.substringBefore(':')
                    if (!SQLUtils.isEmailRegistered(email))
                        SQLUtils.addUserAccount(email, it.substringAfter(':'))
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
                val token = Security.generateTempPassword()
                TokenStorage.addToken(token, email)
                EmailService.sendPasswordReset(email, token)
                //TODO Tell user to check email
                call.respondRedirect("/login")
            } else {
                //TODO Tell user email is not registered"
                call.respondRedirect("/login")
            }
        }

        //websockets
        //TODO actually implement this websocket for our purposes, this is not directly applicable
        webSocket("/websocket") {
            val thisConnection = WebSocketManager.Connection(this)
            WebSocketManager.addConnection(thisConnection)
            try {
                for (frame in incoming) {
                    val text = (frame as Frame.Text).readText()
                    println("onMessage")
                    outgoing.send(Frame.Text(text))
                    WebSocketManager.sendMessageOnWebSocket(this, "test123")
                }
            } catch (e: ClosedReceiveChannelException) {
                println("onClose ${closeReason.await()}")
                WebSocketManager.removeConnection(thisConnection)
            } catch (e: Throwable) {
                println("onError ${closeReason.await()}")
                WebSocketManager.removeConnection(thisConnection)
                e.printStackTrace()
            }
        }
    }
    kvisionInit()
}