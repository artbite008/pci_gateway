package com.siupay.pci.gateway.validate

import com.siupay.pci.gateway.exception.AccessGatewayException
import com.siupay.pci.tokenservice.dto.CardInfoRequest
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class CardInfoRequestValidatorTest {
    @RelaxedMockK
    lateinit var expireValidator: CardExpireValidator

    @InjectMockKs
    lateinit var cardInfoRequestValidator: CardInfoValidator

    @Test
    fun test() {
        every { expireValidator.cardExpireValidate(any(),any(),any()) } just runs
        cardInfoRequestValidator.cardInfoRequestValidate(CardInfoRequest("tok_fcf6d7f46c5e4fe1803e97e2ea5acf4d79dd2d2335a54e109d95652f994b7980"))

        Assertions.assertThrows(AccessGatewayException::class.java) {
            cardInfoRequestValidator.cardInfoRequestValidate(CardInfoRequest("tok_fcf6d@@@@@7f46c5e4fe1803e97e2ea5acf4d79dd2d2335a54e109d95652f994b7980"))
        }
        Assertions.assertThrows(AccessGatewayException::class.java) {
            cardInfoRequestValidator.cardInfoRequestValidate(CardInfoRequest("tok_fcf6d7f46c5e4fe1803e97e2ea5acf4d79dd2d2335a54e109d95652f994b7980tok_fcf6d7f46c5e4fe1803e97e2ea5acf4d79dd2d2335a54e109d95652f994b7980tok_fcf6d7f46c5e4fe1803e97e2ea5acf4d79dd2d2335a54e109d95652f994b7980"))
        }
    }
}