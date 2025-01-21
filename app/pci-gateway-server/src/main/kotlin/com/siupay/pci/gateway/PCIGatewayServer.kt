package com.siupay.pci.gateway

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.scheduling.annotation.EnableScheduling
import java.util.*
import javax.annotation.PostConstruct


@SpringBootApplication
@EnableDiscoveryClient
@EnableEurekaClient
@EnableScheduling
@EnableApolloConfig(value = ["application", "env"])
//@EnableSystemWarn
class PCIGatewayServer

fun main(args: Array<String>) {
    runApplication<PCIGatewayServer>(*args)
}

@PostConstruct
fun postConstruct() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
}



