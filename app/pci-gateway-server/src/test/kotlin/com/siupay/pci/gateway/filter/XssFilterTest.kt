package com.siupay.pci.gateway.filter

import com.google.common.collect.Maps
import com.siupay.pci.gateway.config.GatewayContext
import com.siupay.pci.gateway.constant.PublicConstant
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.http.HttpStatus
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.http.server.reactive.MockServerHttpResponse
import org.springframework.mock.web.server.MockServerWebExchange
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.HashMap

@ExtendWith(MockKExtension::class)
internal class XssFilterTest {

    @InjectMockKs
    lateinit var xssFilter: XssFilter

    @MockK
    var hashMap: HashMap<String, Any> = Maps.newHashMap()

    @Test
    fun filter() {

        val filterChain = label@ GatewayFilterChain { filterExchange: ServerWebExchange ->
            val request = filterExchange.request
            val headers = filterExchange.response.headers

            Mono.empty()
        }
        val request = MockServerHttpRequest.post("http://localhost/api/v1/pa/test").build()
        val response = MockServerHttpResponse()
        response.statusCode = HttpStatus.OK
        val exchange = MockServerWebExchange.from(request)
        ReflectionTestUtils.setField(exchange, "response", response)
        val jsonBody = "{\n" +
                "    \"order\": \"{\\\"requestId\\\":\\\" 7834178097108957053\\\",\\\"requestTime\\\":\\\"1589854999089\\\",\\\"merchantId\\\":\\\"1108483888294029312\\\",\\\"merchantOrderId\\\":\\\"23137876487326482\\\",\\\"amount\\\":{\\\"currency\\\":\\\"HKD\\\",\\\"value\\\":\\\"100\\\"},\\\"autoCapture\\\":true,\\\"userId\\\":\\\"1108483888294029312\\\",\\\"user\\\":{\\\"merchantUserId\\\":\\\"1108483888294029312\\\",\\\"userType\\\":\\\"shopline\\\",\\\"personalInfo\\\":{\\\"firstName\\\":\\\"yu\\\",\\\"lastName\\\":\\\"joshua\\\",\\\"email\\\":\\\"506909808@qq.com\\\",\\\"phoneNumber\\\":\\\"18618314202\\\",\\\"phoneCountryCode\\\":\\\"ES\\\",\\\"dateOfBirth\\\":\\\"20210101\\\"}},\\\"descriptor\\\":\\\"test\\\",\\\"returnUrl\\\":\\\"http://localhost:8080\\\",\\\"notifyUrl\\\":\\\"http://localhost:8080\\\",\\\"riskData\\\":{\\\"riskData\\\":\\\"riskData\\\"},\\\"metadata\\\":{\\\"metadata\\\":\\\"metadata\\\"},\\\"additional\\\":{\\\"additional\\\":\\\"additional\\\"}}\",\n" +
                "    \"channel\": \"taixin\",\n" +
                "    \"paymentMethod\": {\n" +
                "        \"type\": \"card\",\n" +
                "        \"card\": {\n" +
                "            \"number\": \"3565586700000200\",\n" +
                "            \"cvc\": \"484\",\n" +
                "            \"expiryMonth\": \"08\",\n" +
                "            \"expiryYear\": \"25\",\n" +
                "            \"name\": \"yujoshua\",\n" +
                "            \"bin\": \"356558\",\n" +
                "            \"last4\": \"0200\",\n" +
                "            \"panToken\": \"\"\n" +
                "        },\n" +
                "        \"billing\": {\n" +
                "            \"address\": {\n" +
                "                \"countryCode\": \"SG\",\n" +
                "                \"state\": \"jkldsajf\",\n" +
                "                \"city\": \"jldjalsdf\",\n" +
                "                \"street\": \"hudaiof\",\n" +
                "                \"postcode\": \"kjiosfs\"\n" +
                "            },\n" +
                "            \"personalInfo\": {\n" +
                "                \"firstName\": \"yu\",\n" +
                "                \"lastName\": \"joshua\",\n" +
                "                \"email\": \"506909808@qq.com\",\n" +
                "                \"phoneNumber\": \"\",\n" +
                "                \"phoneCountryCode\": \"\",\n" +
                "                \"dateOfBirth\": \"\"\n" +
                "            }\n" +
                "        },\n" +
                "        \"savePaymentMethod\": false\n" +
                "    }\n" +
                "}"
        var gatewaycontext = com.siupay.pci.gateway.config.GatewayContext()
        every { hashMap.get("cacheGatewayContext") } returns gatewaycontext
        every { hashMap.put(com.siupay.pci.gateway.constant.PublicConstant.CACHE_REQUEST_BODY_OBJECT_KEY,any()) } returns "cachedRequestBodyObject"
        ReflectionTestUtils.setField(exchange, "attributes", hashMap)
        gatewaycontext.cacheBody=jsonBody
        xssFilter.filter(exchange, filterChain)?.block()

    }
}