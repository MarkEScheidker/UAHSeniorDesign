package com.chargerfuel

import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement

object SQLUtils {
    private const val DB_URL = "jdbc:mysql://localhost:3306/main657432?enabledTLSProtocols=TLSv1.2&useSSL=false"

    //pull database credentials from local file
    private var lines: List<String> = File("/opt/charger_fuel/mysqlPassword.txt").readLines()
    private val USER = lines[0]
    private val PASS = lines[1]

    // create a queryCache for the menu information
    private val queryCache: MutableMap<String, Any> = mutableMapOf()

    private var connection: Connection = DriverManager.getConnection(DB_URL, USER, PASS)

    //TODO add more get/store commands for user accounts
    fun getHashedPW(email: String): String? {
        return try {
            refreshConnection()
            val statement = connection.createStatement()
            val resultSet =
                statement.executeQuery("SELECT PasswordHash FROM UserLogin ul JOIN Password p ON ul.PasswordID = p.PasswordID WHERE UserEmail = '$email'")
            var result: String? = null
            if (resultSet.next()) result = resultSet.getString("PasswordHash")
            resultSet.close()
            statement.close()
            result
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        }
    }

    fun setHashedPassword(email: String, hash: String): Boolean {
        return try {
            refreshConnection()
            val statement = connection.createStatement()
            val updateCount =
                statement.executeUpdate("UPDATE Password p JOIN UserLogin ul ON p.PasswordID = ul.PasswordID SET PasswordHash = '$hash' WHERE ul.UserEmail = '$email'")
            statement.close()
            updateCount > 0
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    fun isEmailRegistered(email: String): Boolean {
        return try {
            refreshConnection()
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery("SELECT * FROM UserLogin WHERE UserEmail = '$email'")
            val exists = resultSet.next()
            resultSet.close()
            statement.close()
            exists
        } catch (e: SQLException) {
            e.printStackTrace()
            return false
        }
    }

    fun addUserAccount(email: String, passwordHash: String) {
        try {
            refreshConnection()
            var statement = connection.prepareStatement(
                "INSERT INTO Password (PasswordHash) VALUES ('$passwordHash')",
                Statement.RETURN_GENERATED_KEYS
            )
            statement.executeUpdate()
            statement.generatedKeys?.let {
                it.next()
                val passwordID = it.getInt(1)
                statement.close()
                statement = connection.prepareStatement(
                    "INSERT INTO UserLogin (UserEmail, PasswordID) VALUES ('$email','$passwordID')"
                )
                statement.executeUpdate()
                statement.close()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    private fun refreshConnection() {
        if (connection.isClosed) connection = DriverManager.getConnection(DB_URL, USER, PASS)
    }
}