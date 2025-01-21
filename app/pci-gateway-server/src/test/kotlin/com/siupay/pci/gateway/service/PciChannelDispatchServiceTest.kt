package com.siupay.pci.gateway.service

import com.siupay.pci.gateway.cardInfoResponse
import com.siupay.pci.gateway.config.PciChannelDispatchConfig
import com.siupay.pci.gateway.dto.AdditionalData
import com.siupay.pci.gateway.pciChannelDispatchRequest
import com.siupay.pci.gateway.util.toJson
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
import java.nio.charset.Charset

@ExtendWith(MockKExtension::class)
class PciChannelDispatchServiceTest {
    @RelaxedMockK
    lateinit var channelClientService: ChannelClientService

    @RelaxedMockK
    lateinit var pciChannelDispatchConfig: com.siupay.pci.gateway.config.PciChannelDispatchConfig

    @RelaxedMockK
    lateinit var tokenService: TokenService

    @InjectMockKs
    lateinit var target: PciChannelDispatchService

    @BeforeEach
    fun before() {
        every { channelClientService.doPost(any()) } returns Triple("{ok}", "201","message")
        every { tokenService.getCardInfo(any()) } returns cardInfoResponse()
    }

    @Test
    fun doDispatch() {

        var response0 = target.doDispatch(pciChannelDispatchRequest().apply {
            additionalData = com.siupay.pci.gateway.dto.AdditionalData().apply {
                tokenKey = ""
            }
        })
        Assertions.assertNotNull(response0)
        response0.doOnSuccess { println("test with data: "+it?.data) }

        var response = target.doDispatch(pciChannelDispatchRequest())
        Assertions.assertNotNull(response)
        response.doOnSuccess { println("test with data: "+it?.data) }

        val request = pciChannelDispatchRequest()
        request.additionalData?.channelUrl = ""
        var response2 = target.doDispatch(request)
        Assertions.assertNotNull(response2)
        response2.doOnSuccess { println("test with data: "+it?.data) }

        every { tokenService.getCardInfo(any()) } returns null
        var response3 = target.doDispatch(pciChannelDispatchRequest())
        response3.doOnSuccess { println("test with data: "+it?.data) }

        every { tokenService.getCardInfo(any()) } throws Exception("")
        Assertions.assertThrows(Exception::class.java) {
            target.doDispatch(pciChannelDispatchRequest())
        }

        every { channelClientService.doPost(any()) } throws  Exception("")
        Assertions.assertThrows(Exception::class.java) {
            target.doDispatch(pciChannelDispatchRequest())
        }
    }

    @Test
    fun test() {
        val rawData =
            "{\\\"clientReferenceInformation\\\":{\\\"code\\\":\\\"54323007\\\"},\\\"paymentInformation\\\":{\\\"card\\\":{\\\"number\\\":\\\"__CARD_NO__\\\",\\\"expirationMonth\\\":\\\"12\\\",\\\"expirationYear\\\":\\\"2025\\\"}},\\\"orderInformation\\\":{\\\"amountDetails\\\":{\\\"currency\\\":\\\"USD\\\",\\\"totalAmount\\\":\\\"144.14\\\"},\\\"billTo\\\":{\\\"address1\\\":\\\"96, powers street\\\",\\\"administrativeArea\\\":\\\"NH\\\",\\\"country\\\":\\\"US\\\",\\\"locality\\\":\\\"Clearwater milford\\\",\\\"firstName\\\":\\\"James\\\",\\\"lastName\\\":\\\"Smith\\\",\\\"phoneNumber\\\":\\\"7606160717\\\",\\\"email\\\":\\\"test@visa.com\\\",\\\"postalCode\\\":\\\"03055\\\"}},\\\"riskInformation\\\":{\\\"auxiliaryData\\\":[{\\\"key\\\":\\\"1\\\",\\\"value\\\":\\\"Test\\\"}]},\\\"merchantDefinedInformation\\\":[{\\\"key\\\":\\\"1\\\",\\\"value\\\":\\\"Test\\\"}]}"
        println("rawData: " + rawData)
        val base64Data = org.springframework.util.Base64Utils.encodeToUrlSafeString(rawData.toByteArray())
        println("base64Data: " + base64Data)
        val plainText = org.springframework.util.Base64Utils.decodeFromUrlSafeString(base64Data)
        println("plainText: " + plainText.toJson())
        println("##################")

        val rawData1 = "{\"Hi\":\"How are You Joshua@163.com\"}"
        println("rawData1: " + rawData1)
        val base64Data1 = org.springframework.util.Base64Utils.encodeToUrlSafeString(rawData1.toByteArray())
        println("base64Data1: " + base64Data1)
        val plainText1 = org.springframework.util.Base64Utils.decodeFromUrlSafeString(base64Data1)
        println("plainText1: " + String(plainText1, Charset.forName("UTF-8")))
        println("##################")
        // test the str with not encode by base64
        try {
            val plainTextWithNotEncodeByBase64 = org.springframework.util.Base64Utils.decodeFromUrlSafeString(rawData1)
            println("plainTextWithNotEncodeByBase64: " + String(plainTextWithNotEncodeByBase64, Charset.forName("UTF-8")))
        } catch (e: Exception) {
            println("plainTextWithNotEncodeByBase64 will error with: "+e.message)
        }
    }
}