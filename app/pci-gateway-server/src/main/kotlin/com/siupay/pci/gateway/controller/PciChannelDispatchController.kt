package com.siupay.pci.gateway.controller


import com.siupay.pci.gateway.dto.PciChannelDispatchRequest
import com.siupay.pci.gateway.dto.PciChannelDispatchResponse
import com.siupay.pci.gateway.exception.AccessGatewayException
import com.siupay.pci.gateway.exception.ChannelExceptionMessage
import com.siupay.pci.gateway.exception.buildMessage
import com.siupay.pci.gateway.service.PciChannelDispatchService
import com.siupay.pci.gateway.util.toJson
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/pci/channel")
class PciChannelDispatchController {
    @Autowired
    lateinit var pciChannelDispatchService: PciChannelDispatchService

    private val log = LoggerFactory.getLogger(com.siupay.pci.gateway.controller.PciChannelDispatchController::class.java)

    @PostMapping("/dispatch")
    fun dispatch(@RequestBody pciChannelDispatchRequest: com.siupay.pci.gateway.dto.PciChannelDispatchRequest?): Mono<com.siupay.pci.gateway.dto.PciChannelDispatchResponse> {
        log.info("#PciChannelDispatchController#dispatch request additionData {}",pciChannelDispatchRequest?.additionalData?.toJson())
        var response: Mono<com.siupay.pci.gateway.dto.PciChannelDispatchResponse> = try {
            pciChannelDispatchService.doDispatch(pciChannelDispatchRequest!!)
        } catch (e: Exception) {
            if (e is AccessGatewayException) {
                // 异常为AccessGatewayException时将渠道侧的错误码和错误信息通过message字段返回给调用方,风控侧根据渠道侧http响应码进行监控和统计
                Mono.error(ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.buildMessage()))
            } else {
                // 其他异常则message字段返回的渠道侧错误码和错误信息为空.
                Mono.error(ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ChannelExceptionMessage().toJson()))
            }
        }
        log.info("#PciChannelDispatchController#dispatch response  {}", response.toJson())
        return response
    }
}