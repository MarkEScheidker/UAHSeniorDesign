package com.chargerfuel

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import io.kvision.remote.kvisionInit
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import java.time.Duration

private const val TIMEOUT: Long = 1000 * 60 * 30
private val sessionCache: MutableMap<String, Long> = mutableMapOf()

fun ApplicationCall.getSession(): UserSession? =
    sessions.get<UserSession>()?.takeIf { sessionCache.containsKey(it.name) }

fun ApplicationCall.validateLogin(user: String, password: String): UserIdPrincipal? =
    if (SQLUtils.checkPassword(user, password)) login(user) else null

fun ApplicationCall.login(user: String): UserIdPrincipal = UserIdPrincipal(user).also {
    val session = UserSession(it.name)
    sessionCache[session.name] = System.currentTimeMillis()
    this.sessions.set(session)
}


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

    routing {
        //Logged In Pages
        authenticate("session") {
            get("/main") { call.respondHtml("main") }
            get("/account") { call.respondHtml("account") }
            post("/getemail") { call.getSession()?.let { call.respondText(it.name) } }
            post("/getphone") {
                call.getSession()?.let { call.respondText(SQLUtils.getPhoneNumber(it.name) ?: "N/A") }
            }
            post("/changepassword") {
                val info = call.construct<PasswordChangeInfo>()
                call.getSession()?.let {
                    if (SQLUtils.checkPassword(it.name, info.oldPassword)) {
                        if (SQLUtils.setPassword(it.name, info.password))
                            call.respondText("info|passS|Password Changed!")
                    } else call.respondText("info|passF|Incorrect Password")
                } ?: call.respondText("info|passF|An Unknown Error Occurred")
            }
            post("/changephone") {
                val info = call.construct<PhoneChangeInfo>()
                call.getSession()?.let {
                    if (SQLUtils.setPhoneNumber(it.name, info.phone))
                        call.respondText("info|phoneS|Phone Number Changed!")
                    else call.respondText("info|phoneF|An Unknown Error Occurred")
                } ?: call.respondText("info|phoneF|An Unknown Error Occurred")
            }
        }

        //Account Login
        get("/") { call.respondRedirect("/login") }
        get("/login") {
            call.getSession()
                ?.let { call.respondRedirect("/main") }
                ?: call.respondHtml("login")
        }
        post("/login") {
            val info = call.construct<LoginInfo>()
            call.validateLogin(info.user, info.password)
                ?.let { call.respondText("redirect: main") }
                ?: call.respondText("info|error|Incorrect Username/Password")
        }
        get("/logout") {
            call.sessions.get<UserSession>()?.let { sessionCache.remove(it.name) }
            call.sessions.clear<UserSession>()
            call.respondRedirect("/login")
        }

        //Account Creation
        get("/signup") { call.respondHtml("signup") }
        post("/signup") {
            val info = call.construct<AccountCreationInfo>()
            if (!SQLUtils.isAccountRegistered(info.username)) {
                val token = Security.generateSecureToken()
                TokenStorage.addToken(token, AccountVerifyInfo(info.username, info.password.encrypt(), info.phone))
                call.respondText("info|info|Check your email lol") //TODO make a better message
                EmailService.sendEmailValidation(info.email, token)
            } else call.respondText("info|error|Email already registered")
        }
        get("/verify") {
            call.request.queryParameters["id"]
                ?.let { TokenStorage.removeToken<AccountVerifyInfo>(it) }
                ?.let { if (!SQLUtils.isAccountRegistered(it.username)) SQLUtils.addUserAccount(it) }
            call.respondRedirect("/login")
        }

        //Password Resetting
        get("/reset") {
            val id = call.request.queryParameters["id"] ?: run {
                call.respondHtml("forgot")
                return@get
            }
            id.let { TokenStorage.removeToken<PasswordForgotInfo>(it) }
                ?.let {
                    call.login(it.user)
                    call.respondHtml("reset")
                } ?: call.respondRedirect("/reset")
        }
        post("/forgot") {
            val info = call.construct<PasswordForgotInfo>()
            if (SQLUtils.isAccountRegistered(info.user)) {
                val token = Security.generateSecureToken()
                TokenStorage.addToken(token, PasswordForgotInfo(info.user))
                call.respondText("info|info|Check your email lol")
                EmailService.sendPasswordReset(info.email, token)
            } else call.respondText("info|error|Username not found")
        }
        post("/reset") {
            call.getSession()?.let {
                val parameters = call.construct<PasswordResetInfo>()
                if (SQLUtils.setPassword(it.name, parameters.password)) call.respondText("redirect: login")
            } ?: call.respondText("info|error|An Unknown Error Occurred")
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