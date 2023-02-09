package com.chargerfuel

import java.io.File
import java.sql.*

object SQLUtils {
    private const val DB_URL = "jdbc:mysql://localhost/main657432"

    //pull database credentials from local file
    private var lines:List<String> = File("mysqlPassword.txt").readLines()
    private val USER = lines[0]
    private val PASS = lines[1]

    // Connection object
    private var conn: Connection? = null

    // create a queryCache for the menu information
    private val queryCache: MutableMap<String, Any> = mutableMapOf()

    //initialize the database connection in a try catch
    init {
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //TODO add more get/store commands for user accounts
    fun getHashedPW(username: String): String? {
        //TODO write a sql query that gets a user's hashed password, return null if user not found
        return null
    }
    fun setHashedPW(username: String, hashedpw: String): Boolean{
        //todo write a function that sets a user's hashed password, return 1 for success, return 0 for user not found
        return false
    }

    /*
    // A static map to store the query results
    private val queryCache: MutableMap<String, Any> = mutableMapOf()

    // Function to retrieve data from the database
    fun retrieveDataFromDB(query: String): Any {
        // Check if the query result is already in the cache
        if (queryCache.containsKey(query)) {
            // If it is, return the cached result
            return queryCache[query]!!
        }
        // If the result is not in the cache, execute the query and store the result
        val result = executeQuery(query)
        queryCache[query] = result
        return result
    }

    // Function to execute the query and retrieve the result
    fun executeQuery(query: String): Any {
        // Implementation to execute the query and retrieve the result
        // ...
        // ...
        // Return the result
        return result
    }
     */


}