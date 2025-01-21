package com.siupay.pci.gateway.filter.global

import com.siupay.common.api.exception.ErrorCode
import com.siupay.pci.gateway.config.PciChannelDispatchConfig
import com.siupay.pci.gateway.constant.PublicConstant
import com.siupay.pci.gateway.dto.PciChannelDispatchResponse
import com.siupay.pci.gateway.handler.AccessLimitHandler
import com.siupay.pci.gateway.util.GsonUtils.toJson
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import org.springframework.web.util.pattern.PathPattern
import org.springframework.web.util.pattern.PathPatternParser
import reactor.core.publisher.Mono


@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class AuthenticateFilter(
    private val accessLimitFilter: AccessLimitHandler,
    private val pciChannelDispatchConfig: com.siupay.pci.gateway.config.PciChannelDispatchConfig
) : WebFilter {

    private val pathPattern: PathPattern = PathPatternParser().parse("/pci/channel/dispatch")
    private val log = LoggerFactory.getLogger(AuthenticateFilter::class.java)

    override fun filter(serverWebExchange: ServerWebExchange, webFilterChain: WebFilterChain): Mono<Void> {
        val request = serverWebExchange.request

        if (!pathPattern.matches(request.path.pathWithinApplication())) {
            return webFilterChain.filter(serverWebExchange)
        }
        val apiKey = request.headers.getFirst(com.siupay.pci.gateway.constant.PublicConstant.HEADER_API_KEY)
        if (StringUtils.isEmpty(apiKey)) {
            return serverWebExchange.response.writeWith(Mono.just(serverWebExchange.response.bufferFactory()
                    .wrap(toJson(com.siupay.pci.gateway.dto.PciChannelDispatchResponse(ErrorCode.BUSINESS_ERROR.code, ErrorCode.BUSINESS_ERROR.msg)).toByteArray())))
        }
        log.info("PCI-API-KEY is $apiKey")
        return if (!pciChannelDispatchConfig.apiKey.equals(apiKey)) {
            serverWebExchange.response.writeWith(Mono.just(serverWebExchange.response.bufferFactory()
                    .wrap(toJson(com.siupay.pci.gateway.dto.PciChannelDispatchResponse(ErrorCode.BUSINESS_ERROR.code, ErrorCode.BUSINESS_ERROR.msg)).toByteArray())))
        } else webFilterChain.filter(serverWebExchange)
    }

}