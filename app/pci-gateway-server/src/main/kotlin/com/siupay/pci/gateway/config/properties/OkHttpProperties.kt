package com.siupay.pci.gateway.config.properties

import lombok.Data
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.stereotype.Component


@ConfigurationProperties(prefix = "okhttp.client")
@Component
@Data
@RefreshScope
class OkHttpProperties {
     var readTimeout: Int = 4
     var writeTimeout: Int = 4
     var connectTimeout: Int = 4
     var maxIdleConnections: Int? = null
     var keepaliveDuration: Int? = null
     var proxySwitch: String? =null
     var proxyIp: String? =null
     var proxyPort: Int? =null
}