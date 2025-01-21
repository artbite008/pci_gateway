package com.siupay.pci.gateway.dto

import org.hibernate.validator.constraints.Length
import javax.validation.constraints.NotEmpty

data class BindCardRequest(
    /**
     * 渠道侧card id.
     */
    var channelCardId: String? = null,

    /**
     * 请求id
     */
    var requestId: @NotEmpty(message = "requestId is empty") @Length(
        max = 200,
        message = "requestId max length is {max}"
    ) String,

    /**
     * 卡号
     */
    var cardNumber: @NotEmpty(message = "cardNumber is empty") String,

    /**
     * cvv信息
     */
    var cvv: String? = null,
    /**
     * 有效期 年份
     */
    var expiryYear: @NotEmpty(message = "period is empty") String,

    /**
     * 有效期 月份
     */
    var expiryMonth: @NotEmpty(message = "period is empty") String,

    /**
     * 前端传入sessionId
     */
    val sessionId: String? = null
)