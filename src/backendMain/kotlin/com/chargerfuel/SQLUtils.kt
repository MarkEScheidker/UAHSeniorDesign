package com.chargerfuel

import io.ktor.network.sockets.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement
import kotlinx.serialization.json.*

object SQLUtils {
    private const val DB_URL = "jdbc:mysql://localhost:3306/main657432?enabledTLSProtocols=TLSv1.2&useSSL=false"

    //pull database credentials from local file
    private var lines: List<String> = File("/opt/charger_fuel/mysqlPassword.txt").readLines()
    private val USER = lines[0]
    private val PASS = lines[1]

    // create a queryCache for the menu information
    private val queryCache: MutableMap<String, Any> = mutableMapOf()

    private var connection: Connection = DriverManager.getConnection(DB_URL, USER, PASS)

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

    fun getPhoneNumber(email: String): String? {
        return try {
            refreshConnection()
            val statement = connection.createStatement()
            val resultSet =
                statement.executeQuery("SELECT PhoneNumber FROM UserLogin ul JOIN PhoneNumber p ON ul.PhoneNumberID = p.PhoneNumberID WHERE UserEmail = '$email'")
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

    fun setPhoneNumber(email: String, PhoneNumber: String): Boolean{
        return try {
            refreshConnection()
            val statement = connection.createStatement()
            val updateCount = statement.executeUpdate("UPDATE PhoneNumber p JOIN UserLogin ul ON p.PhoneNumberID = ul.PhoneNumberID SET PhoneNumber = '$PhoneNumber' WHERE ul.UserEmail = '$email'")
            statement.close()
            updateCount > 0
        }
        catch (e: SQLException) {
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

    /*
    fun getMenuItems(restaurantName: String): String? {
        return try {
            refreshConnection()
            val statement = connection.createStatement()
            //TODO make this query actually work with our database
            val resultSet = statement.executeQuery("SELECT ItemID, Name, Description, Price, Image FROM MenuItems WHERE RestaurantName = '$restaurantName'")
            val items = mutableListOf<MenuItem>()
            while (resultSet.next()) {
                val itemID = resultSet.getInt("ItemID")
                val name = resultSet.getString("Name")
                val description = resultSet.getString("Description")
                val price = resultSet.getDouble("Price")
                val image = resultSet.getBytes("Image")
                items.add(MenuItem(itemID, name, description, price, image))
            }
            val json = Json.encodeToString(items)
            resultSet.close()
            statement.close()
            return json
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        }
    }
    //todo move this data class somewhere else, prefereably in common code
    @Serializable
    data class MenuItem(val itemID: Int, val name: String, val description: String, val price: Double, val image: ByteArray)

     */


    private fun refreshConnection() {
        if (connection.isClosed) connection = DriverManager.getConnection(DB_URL, USER, PASS)
    }
}