package com.chargerfuel

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.kvision.remote.kvisionInit

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

suspend fun ApplicationCall.respondError(id: String = "error") =
    respondText("info|$id|An unknown error occurred, please try again later")


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
                call.construct<PasswordChangeInfo>()?.let { info ->
                    call.getSession()?.let {
                        if (SQLUtils.checkPassword(it.name, info.oldPassword)) {
                            if (SQLUtils.setPassword(it.name, info.password))
                                call.respondText("info|passS|Password Changed!")
                        } else call.respondText("info|passF|Incorrect Password")
                    } ?: call.respondError("passF")
                } ?: call.respondError("passF")
            }
            post("/changephone") {
                call.construct<PhoneChangeInfo>()?.let { info ->
                    call.getSession()?.let {
                        if (SQLUtils.setPhoneNumber(it.name, info.phone))
                            call.respondText("info|phoneS|Phone Number Changed!")
                        else call.respondText("info|phoneF|An Unknown Error Occurred")
                    } ?: call.respondError("phoneF")
                } ?: call.respondError("phoneF")
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
            call.construct<LoginInfo>()?.let { info ->
                call.validateLogin(info.user, info.password)
                    ?.let { call.respondText("redirect: main") }
                    ?: call.respondText("info|error|Incorrect Username/Password")
            } ?: call.respondError()
        }
        get("/logout") {
            call.sessions.get<UserSession>()?.let { sessionCache.remove(it.name) }
            call.sessions.clear<UserSession>()
            call.respondRedirect("/login")
        }

        //Account Creation
        get("/signup") { call.respondHtml("signup") }
        post("/signup") {
            call.construct<AccountCreationInfo>()?.let { info ->
                if (!SQLUtils.isAccountRegistered(info.username)) {
                    val token = Security.generateSecureToken()
                    TokenStorage.addToken(
                        token,
                        AccountVerifyInfo(info.username, info.password.encrypt(), info.phone)
                    )
                    call.respondText("info|info|Check your email lol") //TODO make a better message
                    EmailService.sendEmailValidation(info.email, token)
                } else call.respondText("info|error|Email already registered")
            } ?: call.respondError()

        }
        get("/verify") {
            call.request.queryParameters["id"]
                ?.let { TokenStorage.removeToken<AccountVerifyInfo>(it) }
                ?.let { if (!SQLUtils.isAccountRegistered(it.username)) SQLUtils.addUserAccount(it) }
            call.respondRedirect("/login")
        }

        //Password Resetting
        get("/reset") {
            (call.request.queryParameters["id"] ?: run {
                call.respondHtml("forgot")
                return@get
            }).let { TokenStorage.removeToken<PasswordForgotInfo>(it) }
                ?.let {
                    call.login(it.user)
                    call.respondHtml("reset")
                } ?: call.respondRedirect("/reset")
        }
        post("/forgot") {
            call.construct<PasswordForgotInfo>()?.let { info ->
                if (SQLUtils.isAccountRegistered(info.user)) {
                    val token = Security.generateSecureToken()
                    TokenStorage.addToken(token, PasswordForgotInfo(info.user))
                    call.respondText("info|info|Check your email lol")
                    EmailService.sendPasswordReset(info.email, token)
                } else call.respondText("info|error|Username not found")
            } ?: call.respondError()
        }
        post("/reset") {
            call.getSession()?.let {
                call.construct<PasswordResetInfo>()?.let { info ->
                    if (SQLUtils.setPassword(it.name, info.password)) call.respondText("redirect: login")
                } ?: call.respondError()
            } ?: call.respondError()
        }
    }
    kvisionInit()
}