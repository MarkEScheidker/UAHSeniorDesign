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
        PasswordResetTokens.removeIf { it.timestamp - time > TIMEOUT}
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
        PasswordResetTokens.removeIf { it.timestamp - time > TIMEOUT}
        PasswordResetTokens.forEach{
            if(it.username == username)
            {
                return true
            }
        }
        return false
    }

}