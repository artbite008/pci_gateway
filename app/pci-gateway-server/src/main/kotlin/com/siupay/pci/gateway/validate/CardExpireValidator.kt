package com.siupay.pci.gateway.validate

import com.siupay.common.api.exception.ErrorCode
import com.siupay.pci.gateway.exception.AccessGatewayException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*

@Component
class CardExpireValidator {
    private val log = LoggerFactory.getLogger(CardExpireValidator::class.java)

    fun cardExpireValidate(expiryYear: String, expiryMoth: String, token: String) {
        val now = Calendar.getInstance()
        now.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), 23, 59, 59)

        val expire = Calendar.getInstance()
        // 信用卡过期时间为月底,默认28日为过期日期,不考虑大小月份
        expire.set(expiryYear.toInt() + 2000, expiryMoth.toInt() - 1, 28)
        // 信用卡过期日期往前推1日作为 capture 的时间提前量
        expire.add(Calendar.DAY_OF_MONTH, -1)
        if (now.after(expire)) {
            log.error("token {} with card is expired, expiryYear {},expiryMoth{}", token, expiryYear, expiryMoth)
            throw AccessGatewayException("card is expired", ErrorCode.VALIDATE_ERROR)
        }
    }
}