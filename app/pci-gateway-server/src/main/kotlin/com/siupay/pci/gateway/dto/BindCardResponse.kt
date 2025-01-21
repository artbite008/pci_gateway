package com.siupay.pci.gateway.dto

data class BindCardResponse(
    var requestId: String,
    var token: String,
    var first6Last4: String,
    var expiryYear: String,
    var expiryMonth: String,
    var bin: String? = null,
    var last4: String? = null,
    var prefix6: String? = null,
    var credential: String? = null
)