package com.chargerfuel

import com.twilio.Twilio
import com.twilio.rest.api.v2010.account.Message
import com.twilio.type.PhoneNumber
import java.io.File

object SMS {

    private var lines: List<String> = File("/opt/charger_fuel/twilioCredentials.txt").readLines()
    private val ACCOUNT_SID = lines[0]
    private val SERVICE_SID = lines[1]
    private val TOKEN = lines[2]

    init {
        Twilio.init(ACCOUNT_SID, TOKEN)
    }

    fun sendOrderConfirm(email: String, order_number: Int, eta: Int): Boolean {
        val number: String = SQLUtils.getPhoneNumber(email) ?: return false
        val message: Message = Message.creator(
            PhoneNumber(number),
            SERVICE_SID,
            "Your Charger Fuel order #$order_number has been received, and will be ready in about $eta minutes"
        ).create()
        return true
    }

    fun sendOrderReady(email: String, order_number: Int): Boolean {
        val number: String = SQLUtils.getPhoneNumber(email) ?: return false
        val message: Message =
            Message.creator(PhoneNumber(number), SERVICE_SID, "Your Charger Fuel order #$order_number is ready!")
                .create()
        return true
    }
}