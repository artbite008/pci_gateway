package com.siupay.pci.gateway.service

import com.siupay.common.api.exception.ErrorCode
import com.siupay.pci.gateway.config.properties.BindCardRsaProperties
import com.siupay.pci.gateway.exception.AccessGatewayException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils

@Service
class BindCardService(
    val rsaService: RSAService,
    val bindCardRsaProperties: com.siupay.pci.gateway.config.properties.BindCardRsaProperties
) {
    private val log = LoggerFactory.getLogger(BindCardService::class.java)

    fun sign(uid: String, token: String?): String? {
        return if (StringUtils.isEmpty(uid) || StringUtils.isEmpty(token)) {
            null
        } else {
            val signPlainText = token + SESSION_ID_DELIMITER + uid
            rsaService.encryptByPrivateKey(signPlainText, rsaService.getPrivateKey())
        }
    }
    //PA-2558: PCI security
    fun validateSession(sessionId: String?): String? {
        if (!"ON".equals(bindCardRsaProperties.switch, ignoreCase = true)) {
            log.warn("BindCardValidateService#validateSession session validate switch is off.")
            return null
        }
        return try {
            if (StringUtils.isEmpty(sessionId)) {
                throw AccessGatewayException("sessionId can not be null or empty", ErrorCode.VALIDATE_ERROR)
            }
            val plainText = rsaService.decryptByPrivateKey(sessionId!!, rsaService.getPrivateKey())
            val sessionData: List<String> = plainText.split(SESSION_ID_DELIMITER)
            if (sessionData.isNullOrEmpty() || sessionData.size != 2) {
                throw AccessGatewayException("sessionId is invalid", ErrorCode.VALIDATE_ERROR)
            }
            val sessionCreateTime = sessionData[0].toLong()
            val uid = sessionData[1]
            val now = System.currentTimeMillis()
            if (bindCardRsaProperties.overDueTimeMillis < now - sessionCreateTime) {
                throw AccessGatewayException(SESSION_ID_IS_OVERDUE, ErrorCode.VALIDATE_ERROR)
            }
            log.info(
                "sessionCreateTime {}, now{}, overDueTimeMillis{}, uid {}",
                sessionCreateTime,
                now,
                bindCardRsaProperties.overDueTimeMillis, uid
            )
            return uid
        } catch (e: Exception) {
            log.error("BindCardValidateService#validateSession error", e)
            if (e is AccessGatewayException) {
                throw e
            } else {
                throw AccessGatewayException("session id validate error", ErrorCode.VALIDATE_ERROR)
            }
        }
    }

    companion object {
        const val SESSION_ID_DELIMITER = "__"
        const val SESSION_ID_IS_OVERDUE = "sessionId is overdue"
    }
}