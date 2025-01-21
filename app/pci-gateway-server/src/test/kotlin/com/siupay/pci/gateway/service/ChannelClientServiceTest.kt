package com.siupay.pci.gateway.service

import com.siupay.pci.gateway.constant.PublicConstant.READ_TIMEOUT_MSG
import com.siupay.pci.gateway.constant.PublicConstant.SOCKET_CLOSE
import com.siupay.pci.gateway.constant.PublicConstant.TIMEOUT_MSG
import com.siupay.pci.gateway.dto.ChannelRequest
import com.siupay.pci.gateway.exception.AccessGatewayException
import com.siupay.pci.gateway.util.toJson
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import okhttp3.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ChannelClientServiceTest {
    @RelaxedMockK
    lateinit var okHttpClient: OkHttpClient

    @InjectMockKs
    lateinit var target: ChannelClientService


    @BeforeEach
    fun before() {
        var request = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), com.siupay.pci.gateway.dto.ChannelRequest().apply {
            url = "http://www.baidu.com"
            header = mapOf("head" to "123")
            requestContent = "{hi}"
        }.toJson())
        every { okHttpClient.newCall(any()) } returns mockk()
        every { okHttpClient.newCall(any()).execute() } returns Response.Builder()
            .body(ResponseBody.create(null, "{body}"))
            .request(okhttp3.Request.Builder().url("http://www.baidu.com").post(request).build())
            .addHeader("key", "123")
            .protocol(Protocol.HTTP_2)
            .message("hi")
            .code(200).build()
    }

    @Test
    fun testPost() {
        val response = target.doPost(com.siupay.pci.gateway.dto.ChannelRequest().apply {
            url = "http://www.baidu.com"
            header = mapOf("head" to "123")
            requestContent = "{hi}"
        })
        println(response.toJson())

        var request = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), com.siupay.pci.gateway.dto.ChannelRequest().apply {
            url = "http://www.baidu.com"
            header = mapOf("head" to "123")
            requestContent = "{hi}"
        }.toJson())
        every { okHttpClient.newCall(any()) } returns mockk()
        every { okHttpClient.newCall(any()).execute() } returns Response.Builder()
            .body(ResponseBody.create(null, "{body}"))
            .request(okhttp3.Request.Builder().url("http://www.baidu.com").post(request).build())
            .addHeader("key", "123")
            .protocol(Protocol.HTTP_2)
            .message("hi")
            .code(500).build()

        Assertions.assertThrows(AccessGatewayException::class.java) {
            target.doPost(com.siupay.pci.gateway.dto.ChannelRequest().apply {
                url = "http://www.baidu.com"
                header = mapOf("head" to "123")
                requestContent = "{hi}"
            })
        }

        every { okHttpClient.newCall(any()).execute() } throws Exception(READ_TIMEOUT_MSG)

        Assertions.assertThrows(AccessGatewayException::class.java) {
            target.doPost(com.siupay.pci.gateway.dto.ChannelRequest().apply {
                url = "http://www.baidu.com"
                header = mapOf("head" to "123")
                requestContent = "{hi}"
            })
        }

        every { okHttpClient.newCall(any()).execute() } throws Exception("$TIMEOUT_MSG:$SOCKET_CLOSE")

        Assertions.assertThrows(AccessGatewayException::class.java) {
            target.doPost(com.siupay.pci.gateway.dto.ChannelRequest().apply {
                url = "http://www.baidu.com"
                header = mapOf("head" to "123")
                requestContent = "{hi}"
            })
        }

        every { okHttpClient.newCall(any()).execute() } throws Exception(SOCKET_CLOSE)

        Assertions.assertThrows(AccessGatewayException::class.java) {
            target.doPost(com.siupay.pci.gateway.dto.ChannelRequest().apply {
                url = "http://www.baidu.com"
                header = mapOf("head" to "123")
                requestContent = "{hi}"
            })
        }

        every { okHttpClient.newCall(any()).execute() } throws Exception("SOCKET_CLOSE")

        Assertions.assertThrows(AccessGatewayException::class.java) {
            target.doPost(com.siupay.pci.gateway.dto.ChannelRequest().apply {
                url = "http://www.baidu.com"
                header = mapOf("head" to "123")
                requestContent = "{hi}"
            })
        }

    }
}