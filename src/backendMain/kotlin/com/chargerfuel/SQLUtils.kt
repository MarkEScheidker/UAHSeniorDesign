package com.chargerfuel
import java.sql.*

object SQLUtils {
    //private const val DB_URL = "jdbc:mysql://localhost/database_name"

    // TODO store database credentials locally in a file instead of hard coded
    private const val USER = "jetson"
    private const val PASS = "password"

    // Connection object
    private var conn: Connection? = null

    // create a queryCache for the menu information
    private val queryCache: MutableMap<String, Any> = mutableMapOf()

    init {
        try {
            //conn = DriverManager.getConnection(DB_URL, USER, PASS)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //TODO add more get/store commands for user accounts
    fun GetHashedPW(username: String): String
    {

        return ""
    }

    fun GetPWSalt(username: String): String{

        return ""
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