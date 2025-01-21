package com.siupay.pci.gateway.validate

import com.siupay.common.api.exception.ErrorCode
import com.siupay.pci.gateway.exception.AccessGatewayException
import com.siupay.pci.tokenservice.dto.CardTokenRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*
import java.util.regex.Pattern

@Component
class CardTokenRequestValidator(
    val cardExpireValidator: CardExpireValidator
) {
    private val log = LoggerFactory.getLogger(CardTokenRequestValidator::class.java)

    fun cardTokenRequestValidate(request: CardTokenRequest) {
        if (request.requestId.length != REQUEST_ID_LENGTH) {
            log.error("request.requestId {} requestId is invalid", request.requestId)
            throw AccessGatewayException(ErrorCode.PARAM_ERROR.msg, ErrorCode.PARAM_ERROR)
        }

        if (!Pattern.matches(CARD_NUMBER_PATTERN, request.cardNumber)) {
            log.error("request.requestId {} cardNumber is invalid", request.requestId)
            throw AccessGatewayException(ErrorCode.PARAM_ERROR.msg, ErrorCode.PARAM_ERROR)
        }

        if (!Pattern.matches(CARD_CVV_PATTERN, request.cvv)) {
            log.error("request.requestId {} cvv is invalid", request.requestId)
            throw AccessGatewayException(ErrorCode.PARAM_ERROR.msg, ErrorCode.PARAM_ERROR)
        }

        if (!Pattern.matches(CARD_EXPIRE_YEAR_PATTERN, request.expiryYear)) {
            log.error("request.requestId {} expiryYear is invalid", request.requestId)
            throw AccessGatewayException(ErrorCode.PARAM_ERROR.msg, ErrorCode.PARAM_ERROR)
        }

        if (!Pattern.matches(CARD_EXPIRE_MONTH_PATTERN, request.expiryMonth)) {
            log.error("request.requestId {} expiryMonth is invalid", request.requestId)
            throw AccessGatewayException(ErrorCode.PARAM_ERROR.msg, ErrorCode.PARAM_ERROR)
        }

        if (request.channelCardId != null && !Pattern.matches(CARD_CHANNEL_CARD_ID_PATTERN, request.channelCardId!!)) {
            log.error(
                "request.requestId {} channelCardId is invalid, channelCardId {}",
                request.requestId,
                request.channelCardId
            )
            throw AccessGatewayException(ErrorCode.PARAM_ERROR.msg, ErrorCode.PARAM_ERROR)
        }
        cardExpireValidator.cardExpireValidate(request.expiryYear, request.expiryMonth, request.requestId)
    }

    companion object {
        const val REQUEST_ID_LENGTH = 36
        const val CARD_NUMBER_PATTERN = "^([1-9][0-9]{11,18})\$"
        const val CARD_CVV_PATTERN = "^([0-9]{3,4})\$"
        const val CARD_EXPIRE_YEAR_PATTERN = "^[0-9]{2}\$"
        const val CARD_EXPIRE_MONTH_PATTERN = "^(0[1-9]|1[0-2])\$"
        const val CARD_CHANNEL_CARD_ID_PATTERN = "^[a-zA-Z0-9_]{10,60}\$"
    }
}