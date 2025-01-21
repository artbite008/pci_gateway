package com.siupay.pci.gateway.validate

import com.siupay.pci.gateway.exception.AccessGatewayException
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class CardExpireValidatorTest {

    @InjectMockKs
    lateinit var target: CardExpireValidator
    fun test() {
        target.cardExpireValidate("30","08","123")
        Assertions.assertThrows(AccessGatewayException::class.java) {
            target.cardExpireValidate("08","08","123")
        }
        Assertions.assertThrows(AccessGatewayException::class.java) {
            target.cardExpireValidate("22","01","123")
        }
    }
}