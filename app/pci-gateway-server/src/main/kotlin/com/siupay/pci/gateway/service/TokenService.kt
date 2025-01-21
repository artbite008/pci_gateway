package com.siupay.pci.gateway.service

import com.siupay.common.api.exception.ErrorCode
import com.siupay.pci.gateway.config.TokenServiceClientConfiguration
import com.siupay.pci.gateway.exception.AccessGatewayException
import com.siupay.pci.gateway.util.GsonUtils
import com.siupay.pci.gateway.validate.CardInfoValidator
import com.siupay.pci.gateway.validate.CardTokenRequestValidator
import com.siupay.pci.tokenservice.dto.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.client.RestTemplate


@Service
class TokenService {
    private val log = LoggerFactory.getLogger(TokenService::class.java)
    private val PATH_GET_CARD_INFO = "/v1/pci/getCardInfo"
    private val PATH_GET_CARD_TOKEN = "/v1/pci/getCardToken"
    private val PATH_QUERY_TOKEN_INFO = "/v1/pci/queryTokenInfo"


    @Autowired
    lateinit var restTemplate: RestTemplate

    @Autowired
    lateinit var tokenServiceClientConfiguration: com.siupay.pci.gateway.config.TokenServiceClientConfiguration

    @Autowired
    lateinit var validator: CardTokenRequestValidator

    @Autowired
    lateinit var cardInfoRequestValidator: CardInfoValidator

    fun getCardInfo(cardInfoRequest: CardInfoRequest): CardInfoResponse? {
        cardInfoRequestValidator.cardInfoRequestValidate(cardInfoRequest)
        val responseEntity: ResponseEntity<String>
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val postEntity: HttpEntity<CardInfoRequest> = HttpEntity(cardInfoRequest, headers)
        try {
            responseEntity = restTemplate.exchange(
                tokenServiceClientConfiguration.baseUrl + PATH_GET_CARD_INFO,
                HttpMethod.POST, postEntity, String::class.java
            )
            if (StringUtils.isEmpty(responseEntity.body)) {
                throw AccessGatewayException("获取卡信息响应报文为空", ErrorCode.RECORD_NOT_EXIST)
            }
            val cardInfoResponse = GsonUtils.toObject(responseEntity.body, CardInfoResponse::class.java)
            cardInfoRequestValidator.cardInfoResponseValidate(cardInfoResponse)
            return cardInfoResponse
        } catch (e: Exception) {
            log.error("TokenService getCardInfo exception:", e)
            throw AccessGatewayException(e.message ?: "获取卡信息异常", ErrorCode.SERVER_ERROR)
        }
    }

    fun queryTokenInfoByCardToken(request: CardInfoRequest): CardTokenInfoResponse? {
        val responseEntity: ResponseEntity<String>
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val postEntity: HttpEntity<CardInfoRequest> = HttpEntity(request, headers)
        try {
            responseEntity = restTemplate.exchange(
                tokenServiceClientConfiguration.baseUrl + PATH_QUERY_TOKEN_INFO,
                HttpMethod.POST, postEntity, String::class.java
            )
            return if (StringUtils.isEmpty(responseEntity.body)) {
                return null
            } else {
                GsonUtils.toObject(responseEntity.body, CardTokenInfoResponse::class.java)
            }
        } catch (e: Exception) {
            log.error("TokenService queryTokenInfoByCardToken exception:", e)
            throw AccessGatewayException(
                "获取卡token信息异常",
                ErrorCode.SERVER_ERROR
            )
        }
    }

    fun getCardToken(cardTokenRequest: CardTokenRequest): CardTokenResponse? {
        val responseEntity: ResponseEntity<String>
        try {
            log.info("#TokenService#getCardToken externalUserId = {}", cardTokenRequest.requestId)
            validator.cardTokenRequestValidate(cardTokenRequest)
            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON
            val postEntity: HttpEntity<CardTokenRequest> = HttpEntity(cardTokenRequest, headers)
            responseEntity = restTemplate.exchange(
                tokenServiceClientConfiguration.baseUrl + PATH_GET_CARD_TOKEN,
                HttpMethod.POST, postEntity, String::class.java
            )
            log.info("TokenService cardTokenServiceApi.getCardToken result:${responseEntity.body}")
        } catch (e: Exception) {
            log.error("TokenService getCardToken exception:", e)
            if (e is AccessGatewayException) {
                throw e
            }else {
                throw AccessGatewayException("获取卡token失败", ErrorCode.SERVER_ERROR)
            }
        }
        return GsonUtils.toObject(responseEntity.body, CardTokenResponse::class.java)
    }
}