package com.siupay.pci.gateway.service

import com.siupay.common.api.exception.ErrorCode
import com.siupay.pci.gateway.constant.PublicConstant
import com.siupay.pci.gateway.dto.ChannelRequest
import com.siupay.pci.gateway.exception.AccessGatewayException
import com.siupay.pci.gateway.util.toJson
import okhttp3.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.util.CollectionUtils

@Service
class ChannelClientService {
    private val log = LoggerFactory.getLogger(ChannelClientService::class.java)

    @Autowired
    var okHttpClient: OkHttpClient? = null

    @Throws(AccessGatewayException::class)
    fun doPost(channelRequest: com.siupay.pci.gateway.dto.ChannelRequest): Triple<String?, String?, String?> {
        val requestBody = RequestBody.create(JSON, channelRequest.requestContent!!)
        // TODO need remove this for pci.
        log.info("requestBody {}",requestBody.toJson())
        val requestBuild = Request.Builder().url(channelRequest.url!!)
        if (!CollectionUtils.isEmpty(channelRequest.header)) {
            channelRequest.header!!.forEach { (key: String?, value: String?) -> requestBuild.addHeader(key, value) }
        }
        requestBuild.post(requestBody)

        val request = requestBuild.build()
        var response: Response? = null
        return try {
            response = okHttpClient!!.newCall(request).execute()
            val responseCode = response?.code()
            var responseContent: String? = null
            if (response?.body() != null) {
                responseContent = response.body()!!.string()
            }
            if (!response.isSuccessful) {
                throw AccessGatewayException(responseContent?:"渠道响应错误", ErrorCode.CHANNEL_ERROR).apply {
                    channelHttpCode = (response?.code()?: HttpStatus.INTERNAL_SERVER_ERROR.value()).toString()
                    channelHttpResponseMsg = response?.message()
                }
            }
            Triple(responseContent, responseCode?.toString(), response?.message().orEmpty())
        } catch (e: Exception) {
            log.error("向渠道发送post请求失败", e)
            //PA-2588: cybs报警透传
            var accessGatewayException: AccessGatewayException;
            if ((e.message != null && e.message!!.contains(com.siupay.pci.gateway.constant.PublicConstant.READ_TIMEOUT_MSG))
                || (e.cause?.message != null && e.cause!!.message!!.contains(com.siupay.pci.gateway.constant.PublicConstant.READ_TIMEOUT_MSG))
            ) {
                accessGatewayException = AccessGatewayException(com.siupay.pci.gateway.constant.PublicConstant.READ_TIMEOUT_MSG ,ErrorCode.CHANNEL_RESPONSE_TIMEOUT).apply {
                    channelHttpCode = (response?.code()?: HttpStatus.INTERNAL_SERVER_ERROR.value()).toString()
                    channelHttpResponseMsg = e.cause?.message ?: e.message ?: response?.message()
                }
            } else if ((e.message != null && e.message!!.contains(com.siupay.pci.gateway.constant.PublicConstant.TIMEOUT_MSG))
                && ( e.cause?.message != null && e.cause!!.message!!.contains(com.siupay.pci.gateway.constant.PublicConstant.SOCKET_CLOSE))
            ) {
                accessGatewayException = AccessGatewayException(com.siupay.pci.gateway.constant.PublicConstant.READ_TIMEOUT_MSG, ErrorCode.CHANNEL_RESPONSE_TIMEOUT).apply {
                    channelHttpCode = (response?.code()?: HttpStatus.INTERNAL_SERVER_ERROR.value()).toString()
                    channelHttpResponseMsg = e.cause?.message ?: e.message ?: response?.message()
                }
            } else if ((e.message != null && e.message!!.contains(com.siupay.pci.gateway.constant.PublicConstant.TIMEOUT_MSG))
                && ( e.cause?.cause?.message != null && e.cause!!.cause!!.message!!.contains(
                    com.siupay.pci.gateway.constant.PublicConstant.SOCKET_CLOSE
                ))
            ) {
                accessGatewayException = AccessGatewayException(com.siupay.pci.gateway.constant.PublicConstant.READ_TIMEOUT_MSG,ErrorCode.CHANNEL_RESPONSE_TIMEOUT).apply {
                    channelHttpCode = (response?.code()?: HttpStatus.INTERNAL_SERVER_ERROR.value()).toString()
                    channelHttpResponseMsg = e.cause?.cause?.message ?: e.cause?.message ?: e.message ?: response?.message()
                }
            } else if (e is AccessGatewayException) {
                accessGatewayException = e
            }  else {
                accessGatewayException = AccessGatewayException(e.message ?: "请求发送失败", ErrorCode.CHANNEL_CONNECTION_FAILED).apply {
                    channelHttpCode = (response?.code()?: HttpStatus.INTERNAL_SERVER_ERROR.value()).toString()
                    channelHttpResponseMsg = e.message ?: response?.message()
                }
            }
            throw accessGatewayException
        } finally {
            response?.close()
        }
    }

    companion object {
        private val JSON = MediaType.parse("application/json; charset=utf-8")
    }
}