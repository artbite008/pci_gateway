package com.siupay.pci.gateway.config

import com.siupay.pci.gateway.config.properties.BindCardRsaProperties
import com.siupay.pci.gateway.config.properties.OkHttpProperties
import com.siupay.pci.gateway.util.toJson
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class ConfigTest {
    @Test
    fun test() {
        var t = com.siupay.pci.gateway.config.TokenServiceClientConfiguration().apply { baseUrl = "123" }
        println(t.toJson())

        var dispatchConfig = com.siupay.pci.gateway.config.PciChannelDispatchConfig().apply {
             apiKey = ""
             channelUri = emptyMap()
        }
        println(dispatchConfig.toJson())

        var ok = com.siupay.pci.gateway.config.properties.OkHttpProperties().apply {
            readTimeout= 100
            writeTimeout=100
            connectTimeout=100
            maxIdleConnections=1000
            keepaliveDuration=1000
            proxySwitch="close"
            proxyIp=""
            proxyPort=9090
        }
        println(ok.toJson())

        val calender = Calendar.getInstance()
        println(calender.toJson())
        println(calender.get(Calendar.YEAR))
        println(calender.get(Calendar.MONTH))

        val now = Calendar.getInstance()
        now.set(now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH),23,59,59)
        val expire = Calendar.getInstance()
        // 信用卡过期时间为月底,默认28日为过期日期,不考虑大小月份
        expire.set(22 + 2000, 5 - 1, 7)
        // 信用卡过期日期往前推1日作为 capture 的时间提前量
        expire.add(Calendar.DAY_OF_MONTH,-1)
        if(now.after(expire)) {
            println("request.requestId {} card is expired")
        }else {
            println("request.requestId {} card not expired")
        }

        val bindCardRsaProperties = com.siupay.pci.gateway.config.properties.BindCardRsaProperties().apply {
            rsaPublicKey = ""
            rsaPrivateKey = ""
            switch = ""
            overDueTimeMillis = 100
        }
        println(bindCardRsaProperties.toJson())
    }
}