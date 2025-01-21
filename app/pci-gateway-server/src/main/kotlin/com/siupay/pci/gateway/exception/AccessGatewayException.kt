package com.siupay.pci.gateway.exception

import com.siupay.common.api.exception.ErrorCode
import com.siupay.common.api.exception.PaymentException
import com.siupay.pci.gateway.util.toJson

open class AccessGatewayException(override val message: String, val code: ErrorCode) : PaymentException(code, message) {
    var channelHttpCode: String? = null
    var channelHttpResponseMsg: String? = null
}

fun AccessGatewayException.buildMessage(): String {
    val message = ChannelExceptionMessage(
        channelHttpCode = this.channelHttpCode.orEmpty(),
        channelHttpResponseMsg = this.channelHttpResponseMsg.orEmpty()
    )
    return message.toJson()
}

data class ChannelExceptionMessage(
    var channelHttpCode: String = "",
    var channelHttpResponseMsg: String = ""
)