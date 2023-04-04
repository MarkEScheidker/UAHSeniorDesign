package com.chargerfuel

import org.mindrot.jbcrypt.BCrypt
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object SQLUtils {
    private const val DB_URL =
        "jdbc:mysql://localhost:3306/main657432?enabledTLSProtocols=TLSv1.2&useSSL=false&serverTimezone=UTC"

    //pull database credentials from local file
    private val lines: List<String> = File("/opt/charger_fuel/mysqlPassword.txt").readLines()
    private val USER = lines[0]
    private val PASS = lines[1]
    private var connection: Connection = DriverManager.getConnection(DB_URL, USER, PASS)

    private fun getPassword(user: String): String? {
        return try {
            refreshConnection()
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery(
                """
                    SELECT PasswordHash
                    FROM UserLogin
                    WHERE UserEmail = '$user'
                """.trimIndent()
            )
            var result: String? = null
            if (resultSet.next()) result = resultSet.getString("PasswordHash")
            resultSet.close()
            statement.close()
            result ?: getRestaurantPassword(user)
        } catch (e: SQLException) {
            null
        }
    }

    private fun getRestaurantPassword(user: String): String? {
        return try {
            refreshConnection()
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery(
                """
                    SELECT PasswordHash
                    FROM Restaurant
                    WHERE Email = '$user'
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
        val hash = getPassword(user) ?: return false
        return BCrypt.checkpw(password, hash)
    }

    fun setPassword(user: String, hash: String): Boolean {
        return try {
            refreshConnection()
            val statement = connection.createStatement()
            val updateCount =
                statement.executeUpdate(
                    """
                        UPDATE UserLogin 
                        SET PasswordHash = '$hash'
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
        try {
            refreshConnection()
            val statement = connection.prepareStatement(
                """
                    INSERT INTO UserLogin (UserEmail, PasswordHash, PhoneNumber)
                    VALUES ('${info.username}','${info.hash}', '${info.phone}')
                """.trimIndent()
            )
            statement.executeUpdate()
            statement.close()
        } catch (_: SQLException) {
        }
    }

    private data class ItemData(
        val menuID: Int,
        val menuName: String,
        val id: Int,
        val name: String,
        val description: String,
        val price: Int,
        val disabled: Boolean
    )

    private fun getMenu(id: Int): Menu {
        try {
            val name: String
            connection.prepareStatement(
                """
                    SELECT Name
                    FROM Restaurant
                    WHERE RestaurantID = $id
                """.trimIndent()
            ).executeQuery().run {
                if (!next()) return Menu("", mapOf())
                name = getString("Name")
                statement.close()
                close()
            }
            val itemData = mutableListOf<ItemData>()
            connection.prepareStatement(
                """
                    SELECT MenuID, MenuName, ItemID, ItemName, ItemDescription, ItemPrice, disabled
                    FROM Restaurant
                    INNER JOIN Menu USING (RestaurantID)
                    INNER JOIN Item USING (MenuID)
                    WHERE RestaurantID = $id
                """.trimIndent()
            ).executeQuery().run {
                while (next()) {
                    itemData.add(
                        ItemData(
                            getInt("MenuID"),
                            getString("MenuName"),
                            getInt("ItemID"),
                            getString("ItemName"),
                            getString("ItemDescription"),
                            getInt("ItemPrice"),
                            getBoolean("disabled")
                        )
                    )
                }
                statement.close()
                close()
            }
            return Menu(name, itemData.associate {
                it.menuID to SubMenu(
                    it.menuName,
                    itemData.filter { item -> item.menuID == it.menuID }
                        .associate { item -> item.id to Item(item.name, item.description, item.price, item.disabled) })
            })
        } catch (e: SQLException) {
            return Menu("", mapOf())
        }
    }

    fun getRestaurantMenus(): Map<Int, Menu> {
        try {
            refreshConnection()
            val menus = mutableMapOf<Int, Menu>()
            connection.prepareStatement(
                """
                    SELECT RestaurantID
                    FROM Restaurant
                """.trimIndent()
            ).executeQuery().run {
                while (next()) {
                    val id = getInt("RestaurantID")
                    menus[id] = getMenu(id)
                }
                statement.close()
                close()
            }
            return menus
        } catch (e: SQLException) {
            return mutableMapOf()
        }
    }

    fun getRestaurants(): Map<String, Boolean> {
        try {
            refreshConnection()
            val restaurants = mutableMapOf<String, Boolean>()
            connection.prepareStatement(
                """
                    SELECT Email
                    FROM Restaurant
                """.trimIndent()
            ).executeQuery().run {
                while (next()) {
                    val name = getString("Email")
                    restaurants[name] = false
                }
                statement.close()
                close()
            }
            return restaurants
        } catch (e: SQLException) {
            return mutableMapOf()
        }
    }

    private fun refreshConnection() {
        if (connection.isClosed) connection = DriverManager.getConnection(DB_URL, USER, PASS)
    }

    fun isRestaurant(user: String): Boolean {
        return getRestaurantPassword(user) != null
    }
}