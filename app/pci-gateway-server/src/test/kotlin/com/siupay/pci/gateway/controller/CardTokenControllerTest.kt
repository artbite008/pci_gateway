/*
package com.siupay.pci.gateway.controller

import com.siupay.pci.gateway.*
import com.siupay.pci.gateway.dto.BindCardRequest
import com.siupay.pci.gateway.service.BindCardService
import com.siupay.pci.gateway.service.TokenService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class CardTokenControllerTest {
    @RelaxedMockK
    lateinit var tokenService: TokenService

    @RelaxedMockK
    lateinit var bindCardService: BindCardService

    @InjectMockKs
    lateinit var target: CardTokenController

    @BeforeEach
    fun before() {
        every { tokenService.getCardToken ( any())} returns cardTokenResponse()
        every { tokenService.getCardInfo( any()) } returns cardInfoResponse()
        every { bindCardService.validateSession(any())} returns "uid"
        every { bindCardService.sign(any(),any())} returns "credential"
    }

    @Test
    fun getCardToken() {
        val response = target.getCardToken(bindCardRequest())
        Assertions.assertNotNull(response)
        Assertions.assertNotNull(!response.isSuccess)

        val response3 = target.getCardToken(
                com.siupay.pci.gateway.dto.BindCardRequest(
                        requestId = "requestId",
                        expiryYear = "2099",
                        expiryMonth = "12",
                        cardNumber = "6214830065431212",
                        cvv = "123",
                        sessionId = ""
                )
        )
        Assertions.assertNotNull(response3)
        Assertions.assertNotNull(response3.isSuccess)

        every { tokenService.getCardToken ( any())} throws Exception("")
        val response2 = target.getCardToken(bindCardRequest())
        Assertions.assertNotNull(response2)
        Assertions.assertTrue(!response2.isSuccess)
    }
}*/
