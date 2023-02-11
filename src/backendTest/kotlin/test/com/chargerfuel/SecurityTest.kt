package test.com.chargerfuel
import com.chargerfuel.Security
import org.junit.Test
class SecurityTest {
    @Test
    fun test_generateXByteKey(){
        val key = bygenerateXByteKey(1)
        val key = bygenerateXByteKey(64)
    }

}