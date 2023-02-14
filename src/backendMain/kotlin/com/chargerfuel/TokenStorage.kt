package com.chargerfuel

object TokenStorage {
    private const val TIMEOUT = 1800000 //30 minutes in ms
    data class TokenData (val username: String, val timestamp: Long, val token: String)
    private var PasswordResetTokens = mutableListOf<TokenData>()
    private var AccountCreationTokens = mutableListOf<TokenData>()

    fun storePWToken(username: String, token: String){
        PasswordResetTokens.removeIf {it.username == username }
        PasswordResetTokens.add(TokenData(username,System.currentTimeMillis(),token))
    }

    fun retrievePWToken(username: String): String?{
        val time = System.currentTimeMillis()
        PasswordResetTokens.removeIf { time - it.timestamp > TIMEOUT}
        PasswordResetTokens.forEach{
            if(it.username == username)
            {
                return it.token
            }
        }
        return null
    }

    fun doesPWTokenExist(username: String): Boolean{
        val time = System.currentTimeMillis()
        PasswordResetTokens.removeIf { time - it.timestamp > TIMEOUT}
        PasswordResetTokens.forEach{
            if(it.username == username)
            {
                return true
            }
        }
        return false
    }

    fun storeAccToken(username: String, token: String){
        AccountCreationTokens.removeIf {it.username == username }
        AccountCreationTokens.add(TokenData(username,System.currentTimeMillis(),token))
    }

    fun retrieveAccToken(username: String): String?{
        val time = System.currentTimeMillis()
        AccountCreationTokens.removeIf { time - it.timestamp > TIMEOUT}
        AccountCreationTokens.forEach{
            if(it.username == username)
            {
                return it.token
            }
        }
        return null
    }

    fun doesAccTokenExist(username: String): Boolean{
        val time = System.currentTimeMillis()
        AccountCreationTokens.removeIf { time - it.timestamp > TIMEOUT}
        AccountCreationTokens.forEach{
            if(it.username == username)
            {
                return true
            }
        }
        return false
    }
}