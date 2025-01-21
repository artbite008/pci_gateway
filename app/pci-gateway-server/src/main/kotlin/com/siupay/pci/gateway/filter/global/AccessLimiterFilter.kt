package com.siupay.pci.gateway.filter.global

import com.siupay.common.api.exception.ErrorCode
import com.siupay.pci.gateway.dto.PciChannelDispatchResponse
import com.siupay.pci.gateway.enums.LimitLockKeyEnum
import com.siupay.pci.gateway.handler.AccessLimitHandler
import com.siupay.pci.gateway.util.GsonUtils.toJson
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import org.springframework.web.util.pattern.PathPattern
import org.springframework.web.util.pattern.PathPatternParser
import reactor.core.publisher.Mono


@Component
@Order(Ordered.LOWEST_PRECEDENCE)
class AccessLimiterFilter(private val accessLimitFilter: AccessLimitHandler) : WebFilter {

    /** 绑卡换token 接口**/
    private val pathPattern: PathPattern = PathPatternParser().parse("/v1/card/getCardToken")

    override fun filter(serverWebExchange: ServerWebExchange, webFilterChain: WebFilterChain): Mono<Void> {
        try {
            if (!pathPattern.matches(serverWebExchange.request.path.pathWithinApplication())) {
                return webFilterChain.filter(serverWebExchange)
            }
            /** 绑卡换token 接口IP限频 **/
            accessLimitFilter.handleRequest(serverWebExchange, com.siupay.pci.gateway.enums.LimitLockKeyEnum.IP)
            /** 绑卡换token 接口用户日内限频 **/
            accessLimitFilter.handleRequest(serverWebExchange, com.siupay.pci.gateway.enums.LimitLockKeyEnum.USER)
        } catch (e: Exception) {
            return serverWebExchange.response.writeWith(
                Mono.just(
                    serverWebExchange.response.bufferFactory().wrap(
                        toJson(
                                com.siupay.pci.gateway.dto.PciChannelDispatchResponse(ErrorCode.BUSINESS_ERROR.code, e.message)
                        ).toByteArray()
                    )
                )
            )
        }
        return webFilterChain.filter(serverWebExchange)
    }
}