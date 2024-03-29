package com.chargerfuel

import java.io.File
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

private const val host = "smtp.gmail.com"

object EmailService {
    private val lines: List<String> = File("/opt/charger_fuel/emailCredentials.txt").readLines()
    private val from = lines[0]
    private val password = lines[1]
    private val session: Session

    init {
        val properties: Properties = System.getProperties()
        properties["mail.smtp.host"] = host
        properties["mail.smtp.port"] = "465"
        properties["mail.smtp.ssl.enable"] = "true"
        properties["mail.smtp.auth"] = "true"

        session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(from, password)
            }
        })
    }

    fun sendEmailValidation(email: String, hash: String) {
        try {
            val message = MimeMessage(session)
            message.setFrom(InternetAddress(from))
            message.addRecipient(Message.RecipientType.TO, InternetAddress(email))
            message.subject = "Charger Fuel Email Verification"
            message.setText("Please click the link below to verify your email.\n\nhttps://charger.food.is/verify?id=$hash")
            Transport.send(message)
        } catch (mex: MessagingException) {
            mex.printStackTrace()
        }
    }

    fun sendPasswordReset(email: String, hash: String) {
        try {
            val message = MimeMessage(session)
            message.setFrom(InternetAddress(from))
            message.addRecipient(Message.RecipientType.TO, InternetAddress(email))
            message.subject = "Charger Fuel Password Reset"
            message.setText("Please click the link below to change your password.\n\nhttps://charger.food.is/reset?id=$hash")
            Transport.send(message)
        } catch (mex: MessagingException) {
            mex.printStackTrace()
        }
    }
}