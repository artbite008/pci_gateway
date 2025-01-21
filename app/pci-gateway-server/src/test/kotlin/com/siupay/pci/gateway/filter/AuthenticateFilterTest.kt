package com.siupay.pci.gateway.filter


import com.google.common.collect.Maps
import com.siupay.pci.gateway.config.PciChannelDispatchConfig
import com.siupay.pci.gateway.filter.global.AuthenticateFilter
import com.siupay.pci.gateway.handler.AccessLimitHandler
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

import org.springframework.http.HttpStatus
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.http.server.reactive.MockServerHttpResponse
import org.springframework.mock.web.server.MockServerWebExchange
import org.springframework.test.util.ReflectionTestUtils

import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.nio.charset.Charset
import java.util.*

@ExtendWith(MockKExtension::class)
internal class AuthenticateFilterTest {
    @InjectMockKs
    lateinit var authenticateFilter: AuthenticateFilter

    @MockK
    lateinit var pciChannelDispatchConfig: com.siupay.pci.gateway.config.PciChannelDispatchConfig

    @MockK
    lateinit var accessLimitFilter: AccessLimitHandler

    @MockK
    var hashMap: HashMap<String, Any>? = Maps.newHashMap()

    @Test
    fun filter() {
        val filterChain = label@ WebFilterChain { filterExchange: ServerWebExchange ->
                val request = filterExchange.request
                val headers = filterExchange.response.headers

            Mono.empty()
        }

        every { accessLimitFilter.handleRequest(any(),any())} just runs

        every {(pciChannelDispatchConfig.apiKey)} returns "L2elxRoVqLSQ16GdtBIcUBIKPZL62Dzy"


        val request = MockServerHttpRequest.post("http://localhost/pci/channel/dispatch")
                .header("PCI-API-KEY", "L2elxRoVqLSQ16GdtBIcUBIKPZL62Dzy")
                .build()
        val response = MockServerHttpResponse()
        response.statusCode = HttpStatus.OK
        val exchange = MockServerWebExchange.from(request)
        ReflectionTestUtils.setField(exchange, "response", response)
        val jsonBody = "{\"eventDate\":\"2021-01-22T10:29:19+01:00\",\"eventType\":\"ACCOUNT_HOLDER_STATUS_CHANGE\",\"executingUserKey\":\"Status Update\",\"live\":\"false\",\"pspReference\":\"8816113077525714\",\"content\":{\"accountHolderCode\":\"yytest001\",\"oldStatus\":{\"status\":\"Suspended\",\"processingState\":{\"disabled\":\"false\",\"processedFrom\":{\"currency\":\"SGD\",\"value\":1},\"tierNumber\":1},\"payoutState\":{\"allowPayout\":\"false\",\"disabled\":\"false\"},\"events\":[{\"AccountEvent\":{\"event\":\"RefundNotPaidOutTransfers\",\"executionDate\":\"2021-03-03T09:01:00+01:00\",\"reason\":\"First deadline for providing KYC details passed, deadline triggered to refund all\"}}]},\"newStatus\":{\"status\":\"Inactive\",\"processingState\":{\"disabled\":\"false\",\"processedFrom\":{\"currency\":\"SGD\",\"value\":1},\"tierNumber\":1},\"payoutState\":{\"allowPayout\":\"false\",\"disabled\":\"false\"},\"events\":[{\"AccountEvent\":{\"event\":\"RefundNotPaidOutTransfers\",\"executionDate\":\"2021-03-03T09:01:00+01:00\",\"reason\":\"First deadline for providing KYC details passed, deadline triggered to refund all\"}}]},\"reason\":\"Account holder has been un-suspended manually\"}}"
        val byteBody = jsonBody.toByteArray(Charset.forName("utf-8"))
        ReflectionTestUtils.setField(authenticateFilter, "pciChannelDispatchConfig", pciChannelDispatchConfig)

       // Mockito.`when`<Any>(hashMap?.get("cachedRequestBodyObject")).thenReturn(byteBody)
        ReflectionTestUtils.setField(exchange, "attributes", hashMap)
        authenticateFilter.filter(exchange, filterChain).block()

        every {(pciChannelDispatchConfig.apiKey)} returns ""
        authenticateFilter.filter(exchange, filterChain).block()

        every {(pciChannelDispatchConfig.apiKey)} returns "123"
        authenticateFilter.filter(exchange, filterChain).block()
    }
}