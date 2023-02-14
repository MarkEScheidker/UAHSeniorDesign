package com.chargerfuel

import java.io.File
import java.sql.*

object SQLUtils {
    private const val DB_URL = "jdbc:mysql://localhost/main657432"

    //pull database credentials from local file
    private var lines:List<String> = File("/opt/charger_fuel/mysqlPassword.txt").readLines()
    private val USER = lines[0]
    private val PASS = lines[1]

    // Connection object
    private var connection: Connection? = null

    // create a queryCache for the menu information
    private val queryCache: MutableMap<String, Any> = mutableMapOf()

    //initialize the database connection in a try catch
    init {
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //TODO add more get/store commands for user accounts
    fun getHashedPW(username: String): String? {
        var statement: Statement? = null
        var resultSet: ResultSet? = null

        try {
            statement = connection?.createStatement()
            resultSet = statement?.executeQuery("SELECT PasswordHash FROM UserLogin WHERE UserName='$username'")

            if (resultSet!!.next()) {
                val passwordId = resultSet.getInt("PasswordID")
                resultSet.close()

                resultSet = statement?.executeQuery("SELECT PasswordHash FROM Password WHERE PasswordID=$passwordId")

                if (resultSet!!.next()) {
                    return resultSet.getString("PasswordHash")
                }
            }
            return null

        } catch (e: SQLException) {
            e.printStackTrace()
            return null
        } finally {
            resultSet?.close()
            statement?.close()
        }
    }

    fun setHashedPW(username: String, hashedpw: String): Boolean{
        var statement: Statement? = null
        var resultSet: ResultSet? = null

        try {
            statement = connection?.createStatement()
            resultSet = statement?.executeQuery("SELECT PasswordID FROM UserLogin WHERE UserName='$username'")

            if (resultSet!!.next()) {
                val passwordId = resultSet.getInt("PasswordID")
                statement?.executeUpdate("UPDATE Password SET PasswordHash='$hashedpw' WHERE PasswordID=$passwordId")
                return true
            }
            return false

        } catch (e: SQLException) {
            e.printStackTrace()
            return false
        } finally {
            resultSet?.close()
            statement?.close()
        }
    }

    fun doesUserExist(username: String): Boolean{
        var statement: Statement? = null
        var resultSet: ResultSet? = null

        return try {
            statement = connection?.createStatement()
            resultSet = statement?.executeQuery("SELECT * FROM UserLogin WHERE UserName='$username'")
            resultSet!!.next()
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        } finally {
            resultSet?.close()
            statement?.close()
        }
    }
}