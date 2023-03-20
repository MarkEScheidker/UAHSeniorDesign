package test.com.chargerfuel

import com.chargerfuel.Security
import org.junit.Test
import org.mindrot.jbcrypt.BCrypt

class SecurityTest {
    @Test
    fun test_generateXByteKey() {
        val key1 = Security.generateXByteKey(0)
        val key2 = Security.generateXByteKey(1)
        val key3 = Security.generateXByteKey(64)

        if (key1.isNotEmpty() || key2.size != 1 || key3.size != 64) {
            throw Exception()
        }
    }

    @Test
    fun test_Bcrypt() {
        val password = "Password1!"
        val hashed = BCrypt.hashpw(password, BCrypt.gensalt())
        val matching = BCrypt.checkpw(password, hashed)
        if (!matching) {
            throw Exception()
        }

        val wrongPassword = "Password1!asdf"
        val matching2 = BCrypt.checkpw(wrongPassword, hashed)
        if (matching2) {
            throw Exception()
        }
    }

    @Test
    fun test_generateSecureToken() {
        val token = Security.generateSecureToken()
        if (token.length != 44) {
            throw Exception()
        }
        print(token)
    }

    @Test
    fun generateHashesManually() {
        print(BCrypt.hashpw("test123", BCrypt.gensalt()))
    }
}