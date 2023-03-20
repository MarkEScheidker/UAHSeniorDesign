package com.chargerfuel

import org.mindrot.jbcrypt.BCrypt
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement

object SQLUtils {
    private const val DB_URL = "jdbc:mysql://localhost:3306/main657432?enabledTLSProtocols=TLSv1.2&useSSL=false"

    //pull database credentials from local file
    private val lines: List<String> = File("/opt/charger_fuel/mysqlPassword.txt").readLines()
    private val USER = lines[0]
    private val PASS = lines[1]
    private var connection: Connection = DriverManager.getConnection(DB_URL, USER, PASS)

    private fun getPassword(user: String): String? {
        if (!userValidation(user)) return null
        return try {
            refreshConnection()
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery(
                """
                SELECT PasswordHash
                FROM UserLogin
                INNER JOIN Password USING (PasswordID)
                WHERE UserEmail = '$user'
            """.trimIndent()
            )
            var result: String? = null
            if (resultSet.next()) result = resultSet.getString("PasswordHash")
            resultSet.close()
            statement.close()
            result
        } catch (e: SQLException) {
            null
        }
    }

    fun checkPassword(user: String, password: String): Boolean {
        if (!userValidation(user) || !passwordValidation(password)) return false
        val hash = getPassword(user) ?: return false
        return BCrypt.checkpw(password, hash)
    }

    fun setPassword(user: String, password: String): Boolean {
        if (!userValidation(user) || !passwordValidation(password)) return false
        return try {
            refreshConnection()
            val statement = connection.createStatement()
            val updateCount =
                statement.executeUpdate(
                    """
                    UPDATE Password 
                    INNER JOIN UserLogin USING(PasswordID)
                    SET PasswordHash = '${password.encrypt()}'
                    WHERE UserEmail = '$user'
                """.trimIndent()
                )
            statement.close()
            updateCount > 0
        } catch (e: SQLException) {
            false
        }
    }

    fun getPhoneNumber(user: String): String? {
        if (!userValidation(user)) return null
        return try {
            refreshConnection()
            val statement = connection.createStatement()
            val resultSet =
                statement.executeQuery("SELECT PhoneNumber FROM UserLogin WHERE UserEmail = '$user'")
            var result: String? = null
            if (resultSet.next()) result = resultSet.getString("PhoneNumber")
            resultSet.close()
            statement.close()
            result
        } catch (e: SQLException) {
            null
        }
    }

    fun setPhoneNumber(user: String, number: String): Boolean {
        if (!userValidation(user) || !phoneValidation(number)) return false
        return try {
            refreshConnection()
            val statement = connection.createStatement()
            val updateCount =
                statement.executeUpdate("UPDATE UserLogin SET PhoneNumber = '$number' WHERE UserEmail = '$user'")
            statement.close()
            updateCount > 0
        } catch (e: SQLException) {
            false
        }
    }

    fun isAccountRegistered(user: String): Boolean {
        if (!userValidation(user)) return false
        return try {
            refreshConnection()
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery("SELECT * FROM UserLogin WHERE UserEmail = '$user'")
            val exists = resultSet.next()
            resultSet.close()
            statement.close()
            exists
        } catch (e: SQLException) {
            return false
        }
    }

    fun addUserAccount(info: AccountVerifyInfo) {
        if (!userValidation(info.username) || !phoneValidation(info.phone) || !passwordValidation(info.password)) return
        try {
            refreshConnection()
            var statement = connection.prepareStatement(
                "INSERT INTO Password (PasswordHash) VALUES ('${info.password}')",
                Statement.RETURN_GENERATED_KEYS
            )
            statement.executeUpdate()
            statement.generatedKeys?.let {
                it.next()
                val passwordID = it.getInt(1)
                statement.close()
                statement = connection.prepareStatement(
                    "INSERT INTO UserLogin (UserEmail, PasswordID, PhoneNumber) VALUES ('${info.username}','$passwordID', '${info.phone}')"
                )
                statement.executeUpdate()
                statement.close()
            }
        } catch (_: SQLException) {
        }
    }

    private fun refreshConnection() {
        if (connection.isClosed) connection = DriverManager.getConnection(DB_URL, USER, PASS)
    }
}