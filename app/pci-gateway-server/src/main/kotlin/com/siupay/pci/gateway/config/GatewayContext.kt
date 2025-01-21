package com.siupay.pci.gateway.config

import org.springframework.util.MultiValueMap


class GatewayContext {

    var cacheBody: String? = null

    var formData: MultiValueMap<String, String>? = null

    var path: String? = null

    companion object {
        const val CACHE_GATEWAY_CONTEXT = "cacheGatewayContext"
    }
}