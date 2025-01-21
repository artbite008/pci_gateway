package com.siupay.pci.gateway.validate

import com.siupay.common.api.exception.ErrorCode
import com.siupay.pci.gateway.cardTokenRequestValidateRequest
import com.siupay.pci.gateway.exception.AccessGatewayException
import com.siupay.pci.tokenservice.dto.CardTokenRequest
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
class CardTokenRequestValidatorTest {

    @InjectMockKs
    lateinit var target: CardTokenRequestValidator

    @RelaxedMockK
    lateinit var expireValidator: CardExpireValidator

    @Test
    fun cardTokenRequestValidate() {
        every { expireValidator.cardExpireValidate(any(),any(),any()) } just runs

        val request = cardTokenRequestValidateRequest()
        target.cardTokenRequestValidate(request)

        Assertions.assertThrows(AccessGatewayException::class.java) {
            val newRequest: CardTokenRequest = cardTokenRequestValidateRequest()
            newRequest.requestId = "123"
            target.cardTokenRequestValidate(newRequest)
        }

        Assertions.assertThrows(AccessGatewayException::class.java) {
            val newRequest: CardTokenRequest = cardTokenRequestValidateRequest()
            newRequest.cardNumber = "uy2837484999444"
            target.cardTokenRequestValidate(newRequest)
        }

        Assertions.assertThrows(AccessGatewayException::class.java) {
            val newRequest: CardTokenRequest = cardTokenRequestValidateRequest()
            newRequest.cardNumber = "45434740022"
            target.cardTokenRequestValidate(newRequest)
        }

        Assertions.assertThrows(AccessGatewayException::class.java) {
            val newRequest: CardTokenRequest = cardTokenRequestValidateRequest()
            newRequest.cardNumber = "45434740022499960000"
            target.cardTokenRequestValidate(newRequest)
        }

        Assertions.assertThrows(AccessGatewayException::class.java) {
            val newRequest: CardTokenRequest = cardTokenRequestValidateRequest()
            newRequest.cvv = "12"
            target.cardTokenRequestValidate(newRequest)
        }

        Assertions.assertThrows(AccessGatewayException::class.java) {
            val newRequest: CardTokenRequest = cardTokenRequestValidateRequest()
            newRequest.cvv = "12889"
            target.cardTokenRequestValidate(newRequest)
        }

        Assertions.assertThrows(AccessGatewayException::class.java) {
            val newRequest: CardTokenRequest = cardTokenRequestValidateRequest()
            newRequest.cvv = "xyz"
            target.cardTokenRequestValidate(newRequest)
        }

        Assertions.assertThrows(AccessGatewayException::class.java) {
            val newRequest: CardTokenRequest = cardTokenRequestValidateRequest()
            newRequest.expiryYear = "110"
            target.cardTokenRequestValidate(newRequest)
        }

        Assertions.assertThrows(AccessGatewayException::class.java) {
            val newRequest: CardTokenRequest = cardTokenRequestValidateRequest()
            newRequest.expiryYear = "xy"
            target.cardTokenRequestValidate(newRequest)
        }

        Assertions.assertThrows(AccessGatewayException::class.java) {
            val newRequest: CardTokenRequest = cardTokenRequestValidateRequest()
            newRequest.expiryYear = "1p"
            target.cardTokenRequestValidate(newRequest)
        }

        Assertions.assertThrows(AccessGatewayException::class.java) {
            val newRequest: CardTokenRequest = cardTokenRequestValidateRequest()
            newRequest.expiryMonth = "1p"
            target.cardTokenRequestValidate(newRequest)
        }

        Assertions.assertThrows(AccessGatewayException::class.java) {
            val newRequest: CardTokenRequest = cardTokenRequestValidateRequest()
            newRequest.expiryMonth = "13"
            target.cardTokenRequestValidate(newRequest)
        }

        Assertions.assertThrows(AccessGatewayException::class.java) {
            val newRequest: CardTokenRequest = cardTokenRequestValidateRequest()
            newRequest.expiryMonth = "1"
            target.cardTokenRequestValidate(newRequest)
        }

        Assertions.assertThrows(AccessGatewayException::class.java) {
            val newRequest: CardTokenRequest = cardTokenRequestValidateRequest()
            newRequest.expiryMonth = "111"
            target.cardTokenRequestValidate(newRequest)
        }

        every { expireValidator.cardExpireValidate(any(),any(),any()) } throws AccessGatewayException("",ErrorCode.VALIDATE_ERROR)
        Assertions.assertThrows(AccessGatewayException::class.java) {
            val newRequest: CardTokenRequest = cardTokenRequestValidateRequest()
            newRequest.expiryYear = "08"
            newRequest.expiryYear = "08"
            target.cardTokenRequestValidate(newRequest)
        }

        Assertions.assertThrows(AccessGatewayException::class.java) {
            val newRequest: CardTokenRequest = cardTokenRequestValidateRequest()
            newRequest.expiryYear = "22"
            newRequest.expiryYear = "01"
            target.cardTokenRequestValidate(newRequest)
        }

        every { expireValidator.cardExpireValidate(any(),any(),any()) } just runs
        val newRequest: CardTokenRequest = cardTokenRequestValidateRequest()
        newRequest.channelCardId = "src_3juarrusguye3jugms74cpvy2m"
        target.cardTokenRequestValidate(newRequest)

        Assertions.assertThrows(AccessGatewayException::class.java) {
            val newRequest: CardTokenRequest = cardTokenRequestValidateRequest()
            newRequest.channelCardId = "src_3+juarrusguye3jugms74cpvy2m"
            target.cardTokenRequestValidate(newRequest)
        }

        Assertions.assertThrows(AccessGatewayException::class.java) {
            val newRequest: CardTokenRequest = cardTokenRequestValidateRequest()
            newRequest.channelCardId = "src_3juarrusguye3jugms74cpvy2msguye3jugms74cpvy2mjuarrusguye3jugms74cpvy2mjuarrusguye3jugms74cpvy2mjuarrusguye3jugms74cpvy2mjuarrusguye3jugms74cpvy2m"
            target.cardTokenRequestValidate(newRequest)
        }

        Assertions.assertThrows(AccessGatewayException::class.java) {
            val newRequest: CardTokenRequest = cardTokenRequestValidateRequest()
            newRequest.channelCardId = "src_3juarrusguye3jugms74cpvy2m@!"
            target.cardTokenRequestValidate(newRequest)
        }
    }
}