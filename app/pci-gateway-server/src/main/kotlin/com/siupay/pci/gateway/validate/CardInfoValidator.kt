package com.siupay.pci.gateway.validate

import com.siupay.common.api.exception.ErrorCode
import com.siupay.pci.gateway.exception.AccessGatewayException
import com.siupay.pci.tokenservice.dto.CardInfoRequest
import com.siupay.pci.tokenservice.dto.CardInfoResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.util.regex.Pattern

@Component
class CardInfoValidator(
    val cardExpireValidator: CardExpireValidator
) {
    private val log = LoggerFactory.getLogger(CardInfoValidator::class.java)

    fun cardInfoRequestValidate(request: CardInfoRequest) {
        if (!Pattern.matches(REQUEST_CARD_INFO_TOKEN, request.token)) {
            log.error("request.token {} is invalid", request.token)
            throw AccessGatewayException(ErrorCode.PARAM_ERROR.msg, ErrorCode.PARAM_ERROR)
        }
    }

    fun cardInfoResponseValidate(cardInfo: CardInfoResponse) {
        if (StringUtils.isEmpty(cardInfo.cardNumber)) {
            throw AccessGatewayException("卡校验失败,卡号为空", ErrorCode.VALIDATE_ERROR)
        }
        if (StringUtils.isEmpty(cardInfo.expiryMonth)) {
            throw AccessGatewayException("卡校验失败,有效期月份为空", ErrorCode.VALIDATE_ERROR)
        }
        if (StringUtils.isEmpty(cardInfo.expiryYear)) {
            throw AccessGatewayException("卡校验失败,有效期年份为空", ErrorCode.VALIDATE_ERROR)
        }
        cardExpireValidator.cardExpireValidate(cardInfo.expiryYear, cardInfo.expiryMonth, cardInfo.cardToken)
    }

    companion object {
        const val REQUEST_CARD_INFO_TOKEN = "^[a-zA-Z0-9_]{30,80}\$"
    }
}