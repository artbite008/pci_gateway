package com.siupay.pci.gateway.constant

import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.declaredMembers

@ExtendWith(MockKExtension::class)
class ConstantTest {

    @Test
    fun test() {
        com.siupay.pci.gateway.constant.PublicConstant::class.declaredMembers.forEach {
            println(it.name )
        }
        com.siupay.pci.gateway.constant.PublicConstant::class.companionObject?.members?.forEach { println(it.name )}

        com.siupay.pci.gateway.constant.RequestParameterKeyConstant::class.declaredMembers.forEach {
            println(it.name )
        }
        com.siupay.pci.gateway.constant.RequestParameterKeyConstant::class.companionObject?.members?.forEach{
            println(it.name)
        }
    }
}