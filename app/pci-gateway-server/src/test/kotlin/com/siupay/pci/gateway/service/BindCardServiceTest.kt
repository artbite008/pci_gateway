package com.siupay.pci.gateway.service

import com.siupay.pci.gateway.config.properties.BindCardRsaProperties
import com.siupay.pci.gateway.exception.AccessGatewayException
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class BindCardServiceTest {
    @RelaxedMockK
    lateinit var rsaService: RSAService

    @RelaxedMockK
    lateinit var bindCardRsaProperties: com.siupay.pci.gateway.config.properties.BindCardRsaProperties

    @InjectMockKs
    lateinit var target: BindCardService

    @BeforeEach
    fun before() {
        val now = System.currentTimeMillis() + 100000
        every { rsaService.decryptByPrivateKey(any(), any()) } returns "$now" + "__" + "12345"
        every { bindCardRsaProperties.overDueTimeMillis } returns 1
        every { bindCardRsaProperties.switch} returns "ON"
    }

    @Test
    fun validateSession() {
        val uid = target.validateSession("12345")
        Assertions.assertNotNull(uid)
        Assertions.assertTrue(uid == "12345")

        every { bindCardRsaProperties.switch} returns "off"
        val uid1 = target.validateSession("12345")
        Assertions.assertNull(uid1)

        every { bindCardRsaProperties.switch} returns "ON"
        assertThrows(AccessGatewayException::class.java) {
            target.validateSession("")
        }

        every { rsaService.decryptByPrivateKey(any(), any()) } returns "_" + "12345"
        assertThrows(AccessGatewayException::class.java) {
            target.validateSession("12345")
        }

        val now = System.currentTimeMillis() - 100000
        every { rsaService.decryptByPrivateKey(any(), any()) } returns "$now" + "__" + "12345"

        assertThrows(AccessGatewayException::class.java) {
            target.validateSession("12345")
        }
    }
}