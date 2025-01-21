package com.siupay.pci.gateway.dto

import lombok.Data

@Data
class PciChannelDispatchResponse {
    var code: String? = null
    var message: String? = null
    var data: String? = null
    var channelResponse: Map<String, String>? = null

    constructor() {}
    constructor(code: String?, message: String?) {
        this.code = code
        this.message = message
    }
}