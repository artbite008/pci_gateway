package com.siupay.pci.gateway.util

import com.siupay.common.api.exception.ErrorCode
import com.siupay.common.api.exception.PaymentException
import com.siupay.pci.gateway.config.properties.RiskProperties
import lombok.extern.slf4j.Slf4j
import org.apache.commons.lang.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.security.InvalidKeyException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec


@Slf4j
@Service
class CyberSourceHeaderUtil(
    val riskProperties: com.siupay.pci.gateway.config.properties.RiskProperties
) {
    companion object {
        const val POST = "POST"
        const val GET = "GET"
        const val PUT = "PUT"
        const val PATCH = "PATCH"
        const val DELETE = "DELETE"
        const val SPACE = " "
        const val HEAD_DATE = "date"
        const val HEAD_HOST = "host"
        const val HEAD_MERCHANT_ID = "v-c-merchant-id"
        const val HEAD_DIGEST = "digest"
        const val HEAD_CONTENT_TYPE = "Content-Type"
        const val HEAD_CONTENT_TYPE_VALUE = "application/json;charset=utf-8"
        const val HEAD_SIGNATURE = "signature"
        const val MERCHANT_ID = "merchantId"
        const val RUN_ENVIRONMENT = "runEnvironment"
        const val MERCHANT_KEY_ID = "merchantKeyId"
        const val MERCHANT_SECRET_KEY = "merchantSecretKey"
        const val REQUEST_TARGET = "requestTarget"
        const val HTTP_METHOD = "httpMethod"

        /**
         * SHA_256 算法
         */
        const val SHA_256 = "SHA-256"

        /**
         * HmacSHA256 算法
         */
        const val HMAC_SHA_256 = "HmacSHA256"
        const val COLON = ": "
        const val LINE_END = "\n"
    }

    private val log = LoggerFactory.getLogger(CyberSourceHeaderUtil::class.java)

    /**
     * @param merchantConfig merchant 配置
     * （应包含 merchantId：商户id，runEnvironment：运行环境，
     * merchantKeyId：商户id序列号，merchantSecretKey：商户密钥，
     * requestTarget：请求uri，httpMethod：请求方式（get\post\..））
     * @param reqBody        请求body的json串
     * @return http请求头
     */
    fun getHeaders(merchantConfig: Map<String, String>, reqBody: String): Map<String, String> {
        //解密

        val map = merchantConfig.toMutableMap()
        map[MERCHANT_KEY_ID] = decrypt(merchantConfig[MERCHANT_KEY_ID])
        map[MERCHANT_ID] = decrypt(merchantConfig[MERCHANT_ID])
        map[MERCHANT_SECRET_KEY] = decrypt(merchantConfig[MERCHANT_SECRET_KEY])

        val newMap = map.toMap()

        val headers: MutableMap<String, String> = HashMap()
        val date = DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneId.of("GMT")))
        headers[HEAD_DATE] = date
        headers[HEAD_HOST] = newMap[RUN_ENVIRONMENT] ?: ""
        headers[HEAD_CONTENT_TYPE] = HEAD_CONTENT_TYPE_VALUE
        headers[HEAD_MERCHANT_ID] = newMap[MERCHANT_ID] ?: ""
        try {
            //根据请求body生成摘要信息
            val digest = generateDigest(reqBody)
            headers[HEAD_DIGEST] = digest
            //生成签名信息
            headers[HEAD_SIGNATURE] = generateSignature(newMap, digest, date)
        } catch (e: Exception) {
            // throw ExceptionFactory.create("生成CyberSource 摘要信息异常", CommonErrorCode.BIZ_ERROR, newMap, reqBody)
            throw PaymentException(ErrorCode.BUSINESS_ERROR, "生成CyberSource 摘要信息异常")
        }
        return headers
    }

    /**
     * 生成签名信息
     *
     * @param merchantConfig merchant 配置
     * @param digest         摘要
     * @param date           当前时间
     * @return 签名信息
     */
    @Throws(InvalidKeyException::class, NoSuchAlgorithmException::class)
    private fun generateSignature(merchantConfig: Map<String, String>, digest: String, date: String): String {
        val signatureHeader = StringBuilder()
        val httpMethod = merchantConfig[HTTP_METHOD] ?: POST
        val signatureValue = signatureGeneration(httpMethod, merchantConfig, digest, date)
        signatureHeader.append("keyid=\"").append(merchantConfig[MERCHANT_KEY_ID]).append("\"")
            .append(", algorithm=\"" + HMAC_SHA_256 + "\"")
            .append(", headers=\"").append(getRequestHeaders(httpMethod.toUpperCase())).append("\"")
            .append(", signature=\"").append(signatureValue).append("\"")
        return signatureHeader.toString()
    }

    /**
     * 生产签名后的加密信息
     *
     * @param httpMethod     - GET/PUT/POST/PATCH/DELETE
     * @param merchantConfig merchant 信息
     * @param digest         摘要信息
     * @param date           时间
     * @return 签名后的加密信息
     * @throws NoSuchAlgorithmException 没有匹配的加密算法异常
     * @throws InvalidKeyException 无效key异常
     */
    @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
    private fun signatureGeneration(
        httpMethod: String,
        merchantConfig: Map<String, String>,
        digest: String,
        date: String
    ): String {
        /*
         * 拼接签名参数
         *host: apitest.cybersource.com
         *date: Thu, 18 Jul 2019 00:18:03 GMT
         *(request-target): post /pts/v2/payments/
         *digest: SHA-256=gXWufV4Zc7VkN9Wkv9jh/JuAVclqDusx3vkyo3uJFWU=
         *v-c-merchant-id: mymerchantid
         */
        val signatureString = StringBuilder()
        signatureString.append(HEAD_HOST.toLowerCase()).append(COLON).append(
            merchantConfig[RUN_ENVIRONMENT]
        ).append(LINE_END)
            .append(HEAD_DATE.toLowerCase()).append(COLON).append(date).append(LINE_END)
            .append("(request-target)").append(COLON)
            .append(getRequestTarget(httpMethod.toUpperCase(), merchantConfig[REQUEST_TARGET]))
            .append(LINE_END)
        if (httpMethod.equals(POST, ignoreCase = true) || httpMethod.equals(
                PUT,
                ignoreCase = true
            ) || httpMethod.equals(
                PATCH, ignoreCase = true
            )
        ) {
            signatureString.append(HEAD_DIGEST.toLowerCase()).append(COLON).append(digest).append(LINE_END)
        }
        signatureString.append(HEAD_MERCHANT_ID).append(COLON).append(
            merchantConfig[MERCHANT_ID]
        )
        val signatureStr = signatureString.toString()
        val secretKey = SecretKeySpec(Base64.getDecoder().decode(merchantConfig[MERCHANT_SECRET_KEY]), HMAC_SHA_256)
        val aKeyId = Mac.getInstance(HMAC_SHA_256)
        aKeyId.init(secretKey)
        aKeyId.update(signatureStr.toByteArray())
        val aHeaders = aKeyId.doFinal()
        return Base64.getEncoder().encodeToString(aHeaders)
    }

    /**
     * @param requestType - GET/PUT/POST/PATCH/DELETE
     * @return request target as per request type.
     */
    private fun getRequestTarget(requestType: String, requestTarget: String?): String? {
        val requestTargetResult: String? = when (requestType) {
            POST -> POST.toLowerCase() + SPACE + requestTarget
            GET -> GET.toLowerCase() + SPACE + requestTarget
            PUT -> PUT.toLowerCase() + SPACE + requestTarget
            DELETE -> DELETE.toLowerCase() + SPACE + requestTarget
            PATCH -> PATCH.toLowerCase() + SPACE + requestTarget
            else -> null
        }
        return requestTargetResult
    }

    /**
     * Get string of request headers to include as part of the request
     *
     * @param requestType must be GET/POST/PUT/PATCH/DELETE
     * @return request headers included according to the request type.
     */
    private fun getRequestHeaders(requestType: String): String? {
        val requestHeader: String = when (requestType) {
            GET, DELETE -> "host date (request-target)" + " " + "v-c-merchant-id"
            POST, PATCH, PUT -> "host date (request-target) digest v-c-merchant-id"
            else -> return null
        }
        return requestHeader
    }

    /**
     * 生成reqBody的摘要信息
     *
     * @param reqBody 请求json
     * @return 摘要信息
     */
    @Throws(NoSuchAlgorithmException::class)
    private fun generateDigest(reqBody: String): String {
        /*
         * This method return Digest value which is SHA-256 hash of payload that is
         * BASE64 encoded
         */
        val digestString = MessageDigest.getInstance(SHA_256)
        val digestBytes = digestString.digest(reqBody.toByteArray(StandardCharsets.UTF_8))
        var bluePrint = Base64.getEncoder().encodeToString(digestBytes)
        bluePrint = SHA_256 + "=" + bluePrint
        return bluePrint
    }

    /**
     * 对称解密
     *
     * @param input 原始数据
     * @return 解密后数据
     */
    fun decrypt(input: String?): String {
        if (StringUtils.isBlank(input)) {
            return ""
        }
        try {
            val cipher: Cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            val keySpec: SecretKey = SecretKeySpec(Base64.getDecoder().decode(riskProperties.key), "AES")
            cipher.init(Cipher.DECRYPT_MODE, keySpec)
            val results: ByteArray = cipher.doFinal(Base64.getDecoder().decode(input))
            return String(results, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            log.error("#CyberSourceHeaderUtil#decrypt decrypt failed：{}", input, e)
        }
        return ""
    }
}