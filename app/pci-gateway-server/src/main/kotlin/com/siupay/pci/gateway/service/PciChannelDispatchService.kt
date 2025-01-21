package com.siupay.pci.gateway.service

import com.siupay.common.api.exception.ErrorCode
import com.siupay.pci.gateway.constant.RequestParameterKeyConstant
import com.siupay.pci.gateway.dto.ChannelRequest
import com.siupay.pci.gateway.dto.PciChannelDispatchRequest
import com.siupay.pci.gateway.dto.PciChannelDispatchResponse
import com.siupay.pci.gateway.util.CyberSourceHeaderUtil
import com.siupay.pci.gateway.util.ValidationUtil
import com.siupay.pci.tokenservice.dto.CardInfoRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import reactor.core.publisher.Mono
import java.nio.charset.Charset
import org.springframework.util.Base64Utils

@Service
class PciChannelDispatchService {
    private val log = LoggerFactory.getLogger(PciChannelDispatchService::class.java)

    @Autowired
    lateinit var channelClientService: ChannelClientService

    @Autowired
    lateinit var tokenService: TokenService

    @Autowired
    lateinit var cyberSourceHeaderUtil: CyberSourceHeaderUtil


    fun doDispatch(dispatchRequest: com.siupay.pci.gateway.dto.PciChannelDispatchRequest): Mono<com.siupay.pci.gateway.dto.PciChannelDispatchResponse> {
        val token = dispatchRequest.additionalData?.tokenKey
        if (StringUtils.isEmpty(token)) {
            return Mono.justOrEmpty(com.siupay.pci.gateway.dto.PciChannelDispatchResponse(ErrorCode.PARAM_ERROR.code, "Token can not be empty!"))
        }
        log.info("PciChannelDispatchService receive request for Token: $token")
        val channelUri = dispatchRequest.additionalData?.channelUrl
        if (StringUtils.isEmpty(channelUri))
            return Mono.justOrEmpty(
                    com.siupay.pci.gateway.dto.PciChannelDispatchResponse(
                            ErrorCode.VALIDATE_ERROR.code,
                            "渠道url不能为空"
                    )
            )
        var response: Triple<String?, String?, String?>?
        val dispatchResponse = com.siupay.pci.gateway.dto.PciChannelDispatchResponse("0000", ErrorCode.SUCCESS.msg)
        try {
            val validationResult = ValidationUtil.validateEntity(dispatchRequest)
            if (validationResult.hasErrors) {
                return Mono.justOrEmpty(
                        com.siupay.pci.gateway.dto.PciChannelDispatchResponse(
                                ErrorCode.VALIDATE_ERROR.code,
                                validationResult.errorMsg.toString()
                        )
                )
            }
            val cardInfoRequest = CardInfoRequest(token = token!!)
            val cardInfoResponse = tokenService.getCardInfo(cardInfoRequest)
                ?: return Mono.justOrEmpty(
                        com.siupay.pci.gateway.dto.PciChannelDispatchResponse(
                                ErrorCode.VALIDATE_ERROR.code,
                                ErrorCode.VALIDATE_ERROR.msg
                        )
                )

            var requestBody = decodeBodyWithCondition(dispatchRequest)
            requestBody =
                requestBody.replace(
                    com.siupay.pci.gateway.constant.RequestParameterKeyConstant.REPLACE_CARD_REGEX,
                    cardInfoResponse.cardNumber,
                    false
                )
                    .replace(com.siupay.pci.gateway.constant.RequestParameterKeyConstant.REPLACE_EXP_YEAR_REGEX, cardInfoResponse.expiryYear, false)
                    .replace(com.siupay.pci.gateway.constant.RequestParameterKeyConstant.REPLACE_EXP_MONTY_REGEX, cardInfoResponse.expiryMonth, false)
            if (cardInfoResponse.cvv != null && requestBody.contains(com.siupay.pci.gateway.constant.RequestParameterKeyConstant.REPLACE_CVV_REGEX)) {
                requestBody = requestBody.replace(
                    com.siupay.pci.gateway.constant.RequestParameterKeyConstant.REPLACE_CVV_REGEX,
                    cardInfoResponse.cvv as String,
                    false
                )
            }
            dispatchRequest.body = requestBody
            val channelRequest = assemble(dispatchRequest, channelUri)
            log.info("PciChannelDispatchService channelRequest is sending for token :$token")
            response = channelClientService.doPost(channelRequest)
        } catch (e: Exception) {
            log.error("PciChannelDispatchService doDispatch exception for token $token:", e)
            throw e
        }
        dispatchResponse.data = response.first
        dispatchResponse.channelResponse = mapOf(
            "responseCode" to response.second.orEmpty(),
            "message" to response.third.orEmpty()
        )
        log.info("PciChannelDispatchService response for token $token, code: ${dispatchResponse.code}, message: ${dispatchResponse.message}, channel-code:${response.second}, channel-message:${response.third}")
        return Mono.justOrEmpty(dispatchResponse)
    }

    private fun assemble(pciChannelDispatchRequest: com.siupay.pci.gateway.dto.PciChannelDispatchRequest, channelUri: String?): com.siupay.pci.gateway.dto.ChannelRequest {
        assembleHeader(pciChannelDispatchRequest)
        val channelRequest = com.siupay.pci.gateway.dto.ChannelRequest()
        channelRequest.requestContent = pciChannelDispatchRequest.body
        channelRequest.url = channelUri
        channelRequest.header = pciChannelDispatchRequest.header
        return channelRequest
    }

    private fun decodeBodyWithCondition(pciChannelDispatchRequest: com.siupay.pci.gateway.dto.PciChannelDispatchRequest) : String {
        return if (CYBERSOURCE.equals(pciChannelDispatchRequest.additionalData?.channelId, ignoreCase = true)
        ) {
            // 风控为了特殊字符过防火墙不被拦截，对body 进行了base64编码
            var cyberSourceBody = pciChannelDispatchRequest.body!!
            try {
                val bytes = Base64Utils.decodeFromUrlSafeString(cyberSourceBody)
                String(bytes, Charset.forName("UTF-8"))
            } catch (e: Exception) {
                log.warn("the base64 decode for the request body is fail, with body:{}", cyberSourceBody)
                cyberSourceBody
            }
        } else {
            pciChannelDispatchRequest.body!!
        }
    }

    private fun assembleHeader(pciChannelDispatchRequest: com.siupay.pci.gateway.dto.PciChannelDispatchRequest) {
        if (CYBERSOURCE.equals(pciChannelDispatchRequest.additionalData?.channelId, ignoreCase = true)
                && pciChannelDispatchRequest.data != null && pciChannelDispatchRequest.body != null
        ) {
            pciChannelDispatchRequest.header = cyberSourceHeaderUtil.getHeaders(pciChannelDispatchRequest.data!!, pciChannelDispatchRequest.body!!)
        }
    }

    companion object {
        const val CYBERSOURCE = "cybersource"
    }
}