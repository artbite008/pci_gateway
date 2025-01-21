package com.siupay.pci.gateway.handler

import com.siupay.common.api.exception.ErrorCode
import com.siupay.common.api.exception.PaymentException
import com.siupay.common.api.utils.UserContextUtils
import com.siupay.pci.gateway.config.properties.AccessLimitProperties
import com.siupay.pci.gateway.enums.LimitLockKeyEnum
import org.apache.commons.lang3.StringUtils
import org.redisson.api.RedissonClient
import org.redisson.client.codec.IntegerCodec
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import java.util.*
import java.util.concurrent.TimeUnit


@Component
class AccessLimitHandler(
    private var client: RedissonClient,
    private val accessProperties: com.siupay.pci.gateway.config.properties.AccessLimitProperties
//    private val warnUtil: WarnClientUtil
) {

    private val log = LoggerFactory.getLogger(AccessLimitHandler::class.java)

    fun handleRequest(serverWebExchange: ServerWebExchange, lockKey: com.siupay.pci.gateway.enums.LimitLockKeyEnum) {
        try {
            when (lockKey) {
                com.siupay.pci.gateway.enums.LimitLockKeyEnum.IP -> {
                    // 根据 IP 进行限流
                    val key = com.siupay.pci.gateway.enums.LimitLockKeyEnum.IP.name + "_" + serverWebExchange.request.remoteAddress
                    val ipLimit = accessLimitCheck(key, accessProperties.seconds, accessProperties.times)
                    if (!ipLimit) {
                        throw PaymentException(ErrorCode.CALL_TOO_FREQUENCY, VISITING_OUR_SERVICE_TOO_FREQUENTLY)
                    }
                }
                com.siupay.pci.gateway.enums.LimitLockKeyEnum.USER -> {
                    // 根据 userId 限流
//                    val userId = Optional.ofNullable(getUserId(serverWebExchange))
                    val userId = Optional.ofNullable(UserContextUtils.getUserId()).orElse(UUID.randomUUID().toString())
                    val userLimit = accessLimitCheck(
                        com.siupay.pci.gateway.enums.LimitLockKeyEnum.USER.name + "_" + userId,
                        accessProperties.bindCardSeconds, accessProperties.bindCardTimes
                    )
                    if (!userLimit) {
//                        warnUtil.putWarnMessage("user-binding-too-frequency","userId=$userId","PCI-GATEWAY")
                        throw PaymentException(ErrorCode.CALL_TOO_FREQUENCY, VISITING_OUR_SERVICE_TOO_FREQUENTLY)
                    }
                }
            }
        } catch (e: PaymentException) {
            throw e
        } catch (e: Exception) {
            log.error("[AccessLimitHandler.handleRequest] 限流检查异常,异常原因:{}", e.message)
        }
    }

    private fun accessLimitCheck(key: String, seconds: Int, maxCount: Int): Boolean {
        var accessCount = 0
        try {
            val mapCache = client.getMapCache<String, Int>("PCI-GATEWAY", IntegerCodec.INSTANCE)
            mapCache.putIfAbsent(key, 0, seconds.toLong(), TimeUnit.SECONDS)
            accessCount = mapCache.addAndGet(key, 1)
        } catch (e: Exception) {
            log.error("[AccessLimitHandler.accessLimitCheck] 限流检查异常")
        }
        return accessCount <= maxCount
    }

    private fun getUserId(serverWebExchange: ServerWebExchange):String{
        val userId = serverWebExchange.session.map{session ->
            val sessionUserId: String = session.getAttribute("SESSION-USER-ID")
            log.info("X-USER-ID: {}",sessionUserId)
            if(StringUtils.isEmpty(sessionUserId)){
                throw PaymentException(ErrorCode.VALIDATE_ERROR, VISITING_OUR_SERVICE_NOT)
            }
            return@map sessionUserId
        }
        return userId.block()
    }

    companion object {
        const val VISITING_OUR_SERVICE_TOO_FREQUENTLY = "You are visiting our service too frequently"
        const val VISITING_OUR_SERVICE_NOT = "You are not login visiting our service"
    }
}