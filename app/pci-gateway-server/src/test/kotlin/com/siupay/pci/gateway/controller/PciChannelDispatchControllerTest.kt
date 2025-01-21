package com.siupay.pci.gateway.controller

import com.siupay.pci.gateway.pciChannelDispatchRequest
import com.siupay.pci.gateway.pciChannelDispatchResponse
import com.siupay.pci.gateway.service.PciChannelDispatchService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Mono

@ExtendWith(MockKExtension::class)
class PciChannelDispatchControllerTest {
    @RelaxedMockK
    lateinit var pciChannelDispatchService: PciChannelDispatchService

    @InjectMockKs
    lateinit var target: PciChannelDispatchController

    @BeforeEach
    fun before() {
        every { pciChannelDispatchService.doDispatch(any()) } returns Mono.justOrEmpty(pciChannelDispatchResponse())
    }

    @Test
    fun dispatch() {
        val response = target.dispatch(pciChannelDispatchRequest())
        Assertions.assertNotNull(response)
    }
}