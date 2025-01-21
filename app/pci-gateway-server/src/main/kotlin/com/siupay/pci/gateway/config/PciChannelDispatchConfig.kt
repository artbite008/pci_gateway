package com.siupay.pci.gateway.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.stereotype.Component
import java.util.*


@Component
@ConfigurationProperties(prefix = "pci.dispatch")
@RefreshScope
class PciChannelDispatchConfig {
    var apiKey: String? = null
    var channelUri: Map<String, String> = HashMap()
}