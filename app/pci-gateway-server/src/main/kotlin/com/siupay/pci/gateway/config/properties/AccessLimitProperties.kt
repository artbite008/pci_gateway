package com.siupay.pci.gateway.config.properties

import lombok.Data
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = "access.limit")
@Component
@Data
@RefreshScope
class AccessLimitProperties {
    /** 限流次数 **/
    var times = 1
    /** 限流次数的时间周期 **/
    var seconds = 10
    /** 单位时间内可绑卡次数 **/
    var bindCardTimes = 10
    /** 绑卡次数统计周期,单位秒,默认：一天 **/
    var bindCardSeconds = 86400
}