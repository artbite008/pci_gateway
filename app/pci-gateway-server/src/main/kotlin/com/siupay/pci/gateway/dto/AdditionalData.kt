package com.siupay.pci.gateway.dto

import javax.validation.constraints.NotBlank


class AdditionalData {
    var channelId: @NotBlank(message = "channelId is empty") String? = null
    var transactionType: @NotBlank(message = "transactionType is empty") String? = null
    var tokenKey: @NotBlank(message = "tokenKey is empty") String? = null // 暂时不rename 成token,cybersource 已经使用该字段
    var channelUrl: @NotBlank(message = "channelUrl is empty") String? = null
}