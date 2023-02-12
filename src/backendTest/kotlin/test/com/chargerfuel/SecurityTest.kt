package test.com.chargerfuel

import com.chargerfuel.Security
import org.junit.Test

class SecurityTest {
    @Test
    fun test_generateXByteKey() {
        val key1 = Security.generateXByteKey(0)
        val key2 = Security.generateXByteKey(1)
        val key3= Security.generateXByteKey(64)

        if(key1.size != 0 || key2.size != 1 || key3.size != 64){
            throw Exception()
        }

        if(key1.size == null || key2.size == null || key3.size == null){
            throw Exception()
        }
    }

    @Test
    fun test_Bcrypt() {
        val password = "Password1!"
        val hashed = Security.generateHashedPassword(password)
        val matching = Security.comparePasswords(password, hashed)
        if(matching == false) {
            throw Exception()
        }

        val wrongPassword = "Password1!asdf"
        val matching2 = Security.comparePasswords(wrongPassword,hashed)
        if(matching2 == true){
            throw Exception()
        }

    }
}