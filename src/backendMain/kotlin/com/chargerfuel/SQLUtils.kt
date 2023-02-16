package com.chargerfuel

import java.io.File
import java.sql.DriverManager
import java.sql.SQLException

object SQLUtils {
    private const val DB_URL = "jdbc:mysql://localhost:3306/main657432?enabledTLSProtocols=TLSv1.2"

    //pull database credentials from local file
    private var lines: List<String> = File("/opt/charger_fuel/mysqlPassword.txt").readLines()
    private val USER = lines[0]
    private val PASS = lines[1]

    // create a queryCache for the menu information
    private val queryCache: MutableMap<String, Any> = mutableMapOf()

    //TODO add more get/store commands for user accounts
    fun getHashedPW(email: String): String? {
        return try {
            val connection = DriverManager.getConnection(DB_URL, USER, PASS)
            val statement = connection.createStatement()
            val resultSet =
                statement.executeQuery("SELECT PasswordHash FROM UserLogin ul JOIN Password p ON ul.PasswordID = p.PasswordID WHERE UserName = '$email'")
            var result: String? = null
            if (resultSet.next()) result = resultSet.getString("PasswordHash")
            resultSet.close()
            statement.close()
            connection.close()
            result
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        }
    }

    fun setHashedPW(username: String, passwordHash: String): Boolean {
        return try {
            val connection = DriverManager.getConnection(DB_URL, USER, PASS)
            val statement = connection.createStatement()
            val updateCount = statement.executeUpdate("UPDATE Password p JOIN UserLogin ul ON p.PasswordID = ul.PasswordID SET PasswordHash = '$passwordHash' WHERE ul.UserName = '$username'")
            statement.close()
            connection.close()
            updateCount > 0
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

//    fun setHashedPW(username: String, hashedpw: String): Boolean{
//        var statement: Statement? = null
//        var resultSet: ResultSet? = null
//
//        try {
//            statement = connection?.createStatement()
//            resultSet = statement?.executeQuery("SELECT PasswordID FROM UserLogin WHERE UserName='$username'")
//
//            if (resultSet!!.next()) {
//                val passwordId = resultSet.getInt("PasswordID")
//                statement?.executeUpdate("UPDATE Password SET PasswordHash='$hashedpw' WHERE PasswordID=$passwordId")
//                return true
//            }
//            return false
//
//        } catch (e: SQLException) {
//            e.printStackTrace()
//            return false
//        } finally {
//            resultSet?.close()
//            statement?.close()
//        }
//    }
//
    fun isEmailRegistered(email: String): Boolean {
        return try {
            val connection = DriverManager.getConnection(DB_URL, USER, PASS)
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery("SELECT * FROM UserLogin WHERE UserEmail='$email'")
            val exists = resultSet.next()
            resultSet.close()
            statement.close()
            connection.close()
            exists
        } catch (e: SQLException) {
            e.printStackTrace()
            return false
        }
    }
}