package com.siupay.pci.gateway.dto

import javax.validation.constraints.NotNull

class PciChannelDispatchRequest {
    var additionalData: @NotNull(message = "additionalData is empty") com.siupay.pci.gateway.dto.AdditionalData? = null
    var header: Map<String, String>? = null
    var data: Map<String, String>? = null // cybersource 已经通过该字段传商户信息，暂不修改该字段
    var body: @NotNull(message = "body is empty") String? = null
}