package com.chargerfuel

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.kvision.remote.kvisionInit
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val TIMEOUT: Long = 1000 * 60 * 30
private val sessionCache: MutableMap<String, Long> = mutableMapOf()

fun ApplicationCall.getSession(): UserSession? =
    sessions.get<UserSession>()?.takeIf { sessionCache.containsKey(it.name) }

fun ApplicationCall.validateLogin(user: String, password: String): UserIdPrincipal? =
    if (SQLUtils.checkPassword(user, password)) login(user) else null

fun ApplicationCall.login(user: String): UserIdPrincipal = UserIdPrincipal(user).also {
    val session = UserSession(it.name)
    sessionCache[session.name] = System.currentTimeMillis()
    sessions.set(session)
}

suspend fun ApplicationCall.respondError(id: String = "error") =
    respondText("info|$id|An unknown error occurred, please try again later")


@Suppress("unused")
fun Application.main() {
    RestaurantStorage
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
            get("/cart") { call.respondHtml("cart") }
            post("/getemail") { call.getSession()?.let { call.respondText(it.name) } }
            post("/getphone") {
                call.getSession()?.let { call.respondText(SQLUtils.getPhoneNumber(it.name) ?: "N/A") }
            }
            post("/changepassword") {
                call.construct<PasswordChangeInfo>()?.let { info ->
                    call.getSession()?.let {
                        if (SQLUtils.checkPassword(it.name, info.oldPassword)) {
                            if (SQLUtils.setPassword(it.name, info.password.encrypt()))
                                call.respondText("info|passS|Password Changed!")
                        } else call.respondText("info|passF|Incorrect Password")
                    } ?: call.respondError("passF")
                } ?: call.respondText("info|passF|Incorrect Password")
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
            post("/getrestaurant") {
                call.construct<GetRestaurantInfo>()?.let { info ->
                    info.restaurant.toIntOrNull()
                        ?.let { id ->
                            RestaurantStorage.getMenu(id)
                                ?.let { call.respondText(Json.encodeToString(it)) }
                                ?: call.respondText("redirect: main")
                        } ?: call.respondText("redirect: main")
                } ?: call.respondError()
            }
            //ordering stuff
            post("/cartadd") {
                call.getSession()?.let {
                    val id = call.receive<String>().toInt()
                    if (it.getCart().containsKey(id)) {
                        val itemAndCount = it.getCart().get(id)
                        val count = itemAndCount?.second ?: 0
                        val itemName = itemAndCount?.first?.name ?: "Unknown Item"
                        call.respondText("info|$id|${count + 1} items added to cart")
                    } else {
                        call.respondText("info|$id|Item Added to Cart")
                    }
                    it.addToCart(id)
                }
            }
            post("/cartremove") {
                call.getSession()?.let {
                    val id = call.receive<String>().toInt()
                    it.removeFromCart(id)
                    call.respondText("info|$id|Item Removed from Cart")
                }
            }
            post("/cartclear") {
                call.getSession()?.let {
                    it.clearCart()
                    call.respondText("redirect: cart")
                }
            }

            post("/getcartsize") {
                call.getSession()?.let { call.respondText("info|cart|${it.getCartSize()}") }
            }
            post("/getcart") {
                call.getSession()?.let { call.respondText(Json.encodeToString(it.getCart())) }
            }
            post("/placeorder") {
                call.getSession()?.let{
                    if(it.placeOrder()){
                        call.respondText("redirect: main")
                    }else {
                        call.respondText("info|warning|Cart cannot contain items from more than one restaurant.")
                    }
                }
            }
            //restaurant account stuff
            get("/resmain") {
                call.getSession()?.let {
                    if (SQLUtils.isRestaurant(it.name)) call.respondHtml("resmain")
                    else call.respondRedirect("main")
                } ?: call.respondRedirect("login")
            }
            get("/orders") {
                call.getSession()?.let {
                    if (SQLUtils.isRestaurant(it.name)) call.respondHtml("orders")
                    else call.respondRedirect("main")
                } ?: call.respondRedirect("login")
            }
            post("/getorders") {
                call.getSession()?.let {
                    if (SQLUtils.isRestaurant(it.name)) call.respondText(Json.encodeToString(getOrders(it.name)))
                    else call.respondText("redirect: main")
                }
            }
            post("/reschangepassword") {
                call.construct<PasswordChangeInfo>()?.let { info ->
                    call.getSession()?.let {
                        if (SQLUtils.checkRestaurantPassword(it.name, info.oldPassword)) {
                            if (SQLUtils.setRestaurantPassword(it.name, info.password.encrypt()))
                                call.respondText("info|passS|Password Changed!")
                        } else call.respondText("info|passF|Incorrect Password")
                    } ?: call.respondError("passF")
                } ?: call.respondText("info|passF|Incorrect Password")
            }
            post("/toggleopenclosed") {
                call.getSession()?.let {
                    if (SQLUtils.isRestaurant(it.name)) {
                        RestaurantState.toggleRestaurantState(it.name)
                    }
                    if (RestaurantState.getRestaurantState(it.name) == true) {
                        call.respondText("info|openorclosed|Restaurant is Open")
                    } else {
                        call.respondText("info|openorclosed|Restaurant is Closed")
                    }
                }
            }
            post("/restaurantstate") {
                call.getSession()?.let {
                    if (SQLUtils.isRestaurant(it.name)) {
                        if (RestaurantState.getRestaurantState(it.name) == true) {
                            call.respondText("info|openorclosed|Restaurant is Open")
                        } else {
                            call.respondText("info|openorclosed|Restaurant is Closed")
                        }
                    }
                }
            }
            post("/completeorder") {
                call.getSession()?.let {
                    if (SQLUtils.isRestaurant(it.name)) {
                        val orderID = call.receive<String>().toIntOrNull() ?: run {
                            call.respondError()
                            return@let
                        }
                        println(orderID)
                        completeOrder(orderID)
                        call.respondText("info|success|order completed")
                    } else call.respondRedirect("main")
                } ?: call.respondRedirect("login")
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
                call.validateLogin(info.username, info.password)
                    ?.run {
                        call.getSession()?.let {
                            if (SQLUtils.isRestaurant(it.name)) call.respondText("redirect: resmain")
                            else call.respondText("redirect: main")
                        }
                    }
                    ?: call.respondText("info|error|Incorrect Username/Password")
            } ?: call.respondText("info|error|Incorrect Username/Password")
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
                    call.respondText("info|info|Check your email")
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
                    if (SQLUtils.setPassword(it.name, info.password.encrypt())) call.respondText("redirect: login")
                } ?: call.respondError()
            } ?: call.respondError()
        }
    }
    kvisionInit()
}