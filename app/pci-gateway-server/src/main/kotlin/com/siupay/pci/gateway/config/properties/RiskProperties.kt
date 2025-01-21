package com.siupay.pci.gateway.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties(prefix = "risk.decrypt")
@RefreshScope
class RiskProperties {
     var key: String = ""
}