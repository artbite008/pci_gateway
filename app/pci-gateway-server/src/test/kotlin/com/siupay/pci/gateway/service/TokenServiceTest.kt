package com.siupay.pci.gateway.service

import com.siupay.pci.gateway.*
import com.siupay.pci.gateway.config.TokenServiceClientConfiguration
import com.siupay.pci.gateway.util.toJson
import com.siupay.pci.gateway.validate.CardInfoValidator
import com.siupay.pci.gateway.validate.CardTokenRequestValidator
import com.siupay.pci.tokenservice.dto.CardInfoRequest
import com.siupay.pci.tokenservice.dto.CardTokenRequest
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.*
import org.springframework.web.client.RestTemplate

@ExtendWith(MockKExtension::class)
class TokenServiceTest {
    @RelaxedMockK
    lateinit var validator: CardTokenRequestValidator
    @RelaxedMockK
    lateinit var restTemplate: RestTemplate
    @RelaxedMockK
    lateinit var tokenServiceClientConfiguration: com.siupay.pci.gateway.config.TokenServiceClientConfiguration
    @RelaxedMockK
    lateinit var cardInfoRequestValidator: CardInfoValidator
    @InjectMockKs
    lateinit var target: TokenService

    @BeforeEach
    fun before() {
        every { tokenServiceClientConfiguration.baseUrl } returns ""
        every { validator.cardTokenRequestValidate(any()) } just runs
        every { cardInfoRequestValidator.cardInfoRequestValidate(any()) } just runs
    }

    @Test
    fun getCardInfo() {
        var json =
            "{\"authCode\":\"\",\"businessCode\":\"\",\"cardNumber\":\"3565586700000200\",\"cardToken\":\"c0f0cc4e-f561-4db5-80e1-68b3f2c0bb12\",\"cardType\":\"Credit\",\"cvv\":\"484\",\"expiryMonth\":\"08\",\"expiryYear\":\"25\",\"holderName\":\"Joshua\",\"procMethod\":\"01\"}"
        println(json)
        val header = HttpHeaders()
        header.contentType = MediaType.APPLICATION_JSON
        every {
            restTemplate.exchange(
                any<String>(),
                any<HttpMethod>(),
                any<HttpEntity<CardInfoRequest>>(),
                any<Class<*>>()
            )
        } returns ResponseEntity<String>(
            json,
            header,
            HttpStatus.OK
        )
        val response = target.getCardInfo(cardInfoRequest())
        Assertions.assertNotNull(response)

        // exceptions
        every {
            restTemplate.exchange(
                any<String>(),
                any<HttpMethod>(),
                any<HttpEntity<CardInfoRequest>>(),
                any<Class<*>>()
            )
        } returns ResponseEntity<String>(
            null,
            header,
            HttpStatus.OK
        )
        Assertions.assertThrows(Exception::class.java) { target.getCardInfo(cardInfoRequest()) }

        json =
            "{\"authCode\":\"\",\"businessCode\":\"\",\"cardNumber\":\"\",\"cardToken\":\"c0f0cc4e-f561-4db5-80e1-68b3f2c0bb12\",\"cardType\":\"Credit\",\"cvv\":\"484\",\"expiryMonth\":\"08\",\"expiryYear\":\"25\",\"holderName\":\"Joshua\",\"procMethod\":\"01\"}"

        every {
            restTemplate.exchange(
                any<String>(),
                any<HttpMethod>(),
                any<HttpEntity<CardInfoRequest>>(),
                any<Class<*>>()
            )
        } throws Exception("")
        Assertions.assertThrows(Exception::class.java) { target.getCardInfo(cardInfoRequest()) }

        json =
            "{\"authCode\":\"\",\"businessCode\":\"\",\"cardNumber\":\"3565586700000200\",\"cardToken\":\"c0f0cc4e-f561-4db5-80e1-68b3f2c0bb12\",\"cardType\":\"Credit\",\"cvv\":\"484\",\"expiryMonth\":\"08\",\"expiryYear\":\"\",\"holderName\":\"Joshua\",\"procMethod\":\"01\"}"
        every {
            restTemplate.exchange(
                any<String>(),
                any<HttpMethod>(),
                any<HttpEntity<CardInfoRequest>>(),
                any<Class<*>>()
            )
        } throws Exception()
        Assertions.assertThrows(Exception::class.java) { target.getCardInfo(cardInfoRequest()) }

        json =
            "{\"authCode\":\"\",\"businessCode\":\"\",\"cardNumber\":\"3565586700000200\",\"cardToken\":\"c0f0cc4e-f561-4db5-80e1-68b3f2c0bb12\",\"cardType\":\"Credit\",\"cvv\":\"484\",\"expiryMonth\":\"\",\"expiryYear\":\"25\",\"holderName\":\"Joshua\",\"procMethod\":\"01\"}"

        every {
            restTemplate.exchange(
                any<String>(),
                any<HttpMethod>(),
                any<HttpEntity<CardInfoRequest>>(),
                any<Class<*>>()
            )
        } throws Exception("")
        Assertions.assertThrows(Exception::class.java) { target.getCardInfo(cardInfoRequest()) }

        every {
            restTemplate.exchange(
                any<String>(),
                any<HttpMethod>(),
                any<HttpEntity<CardInfoRequest>>(),
                any<Class<*>>()
            )
        } throws Exception("")
        Assertions.assertThrows(Exception::class.java) { target.getCardInfo(cardInfoRequest()) }
    }

    @Test
    fun getCardToken() {
        var json = cardTokenResponse().toJson()
        val header = HttpHeaders()
        header.contentType = MediaType.APPLICATION_JSON
        every {
            restTemplate.exchange(
                any<String>(),
                any<HttpMethod>(),
                any<HttpEntity<CardTokenRequest>>(),
                any<Class<*>>()
            )
        } returns ResponseEntity<String>(
            json,
            header,
            HttpStatus.OK
        )
        val response = target.getCardToken(cardTokenRequest())
        Assertions.assertNotNull(response)

        every {
            restTemplate.exchange(
                any<String>(),
                any<HttpMethod>(),
                any<HttpEntity<CardTokenRequest>>(),
                any<Class<*>>()
            )
        } throws Exception("")
        Assertions.assertThrows(Exception::class.java) { target.getCardToken(cardTokenRequest()) }
    }

    @Test
    fun queryTokenInfoByCardToken() {
        var json = cardTokenInfoResponse().toJson()
        val header = HttpHeaders()
        header.contentType = MediaType.APPLICATION_JSON
        every {
            restTemplate.exchange(
                any<String>(),
                any<HttpMethod>(),
                any<HttpEntity<CardInfoRequest>>(),
                any<Class<*>>()
            )
        } returns ResponseEntity<String>(
            json,
            header,
            HttpStatus.OK
        )
        val response = target.queryTokenInfoByCardToken(cardInfoRequest())
        Assertions.assertNotNull(response)

        every {
            restTemplate.exchange(
                any<String>(),
                any<HttpMethod>(),
                any<HttpEntity<CardInfoRequest>>(),
                any<Class<*>>()
            )
        } throws Exception("")
        Assertions.assertThrows(Exception::class.java) { target.queryTokenInfoByCardToken(cardInfoRequest())}
    }
}
