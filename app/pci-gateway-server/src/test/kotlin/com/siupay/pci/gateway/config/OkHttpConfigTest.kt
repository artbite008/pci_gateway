package com.siupay.pci.gateway.config

import com.siupay.pci.gateway.config.properties.OkHttpProperties
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class OkHttpConfigTest {
    @InjectMockKs
    lateinit var target: com.siupay.pci.gateway.config.OkHttpConfig

    @RelaxedMockK
    lateinit var okHttpProperties: com.siupay.pci.gateway.config.properties.OkHttpProperties

    @BeforeEach
    fun before() {
        every { okHttpProperties.connectTimeout } returns 100
        every { okHttpProperties.readTimeout } returns 100
        every { okHttpProperties.writeTimeout } returns 100
        every { okHttpProperties.connectTimeout } returns 100
        every { okHttpProperties.maxIdleConnections } returns 100
        every { okHttpProperties.keepaliveDuration } returns 100
        every { okHttpProperties.proxySwitch } returns ""
        every { okHttpProperties.proxyIp } returns ""
        every { okHttpProperties.proxyPort } returns 200
    }

    @Test
    fun test() {
        val result = target.x509TrustManager()
        val factory = target.sslSocketFactory(result)
        val pool =target.pool(okHttpProperties)
        target.okHttpClient(okHttpProperties,factory,result,pool)

        every { okHttpProperties.proxySwitch } returns "open"
        target.okHttpClient(okHttpProperties,factory,result,pool)
    }
}