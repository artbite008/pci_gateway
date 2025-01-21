package com.siupay.pci.gateway.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "bind.card")
@RefreshScope
class BindCardRsaProperties {
    var rsaPublicKey: String = ""
    var rsaPrivateKey: String = ""
    var switch: String = ""
    var overDueTimeMillis: Long = 12000
}