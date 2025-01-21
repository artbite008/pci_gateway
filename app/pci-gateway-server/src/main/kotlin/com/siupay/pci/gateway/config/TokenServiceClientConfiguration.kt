
package com.siupay.pci.gateway.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = "pa.internalapi.tokenservice")
@Component
@RefreshScope
class TokenServiceClientConfiguration {
    lateinit var baseUrl: String
}

