package test.com.chargerfuel

import com.chargerfuel.Security
import org.junit.Test

class SecurityTest {
    @Test
    fun test_generateXByteKey() {
        val key1 = Security.generateXByteKey(1)
        val key2 = Security.generateXByteKey(64)
    }

}