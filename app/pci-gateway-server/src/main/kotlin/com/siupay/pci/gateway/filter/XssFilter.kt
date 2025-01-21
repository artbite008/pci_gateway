package com.siupay.pci.gateway.filter

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.JsonNodeType
import com.siupay.pci.gateway.config.GatewayContext
import com.siupay.pci.gateway.constant.PublicConstant
import com.siupay.pci.gateway.util.objectMapper
import com.siupay.pci.gateway.util.toJson
import io.netty.buffer.ByteBufAllocator
import org.apache.commons.lang3.StringUtils
import org.apache.commons.text.StringEscapeUtils
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.NettyDataBufferFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpRequestDecorator
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets


/**
 * todo requestParam filter
 */
@Component
class XssFilter : GlobalFilter, Ordered {

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void?>? {
        val requestBody = resolveBodyFromRequest(exchange)
        if(StringUtils.isBlank(requestBody)){
            return chain.filter(exchange)
        }
        val newRequestBody = filterXss(requestBody)
        val bytes = newRequestBody.toByteArray(StandardCharsets.UTF_8)
        val bodyDataBuffer = buffer(bytes)
        val bodyFlux = Flux.just(bodyDataBuffer)
        var newRequest = exchange.request.mutate().uri(exchange.request.uri).build()
        newRequest = object : ServerHttpRequestDecorator(newRequest) {
            override fun getBody(): Flux<DataBuffer> {
                return bodyFlux
            }
        }

        val headers = HttpHeaders()
        headers.putAll(exchange.request.headers)
        val length = bytes.size
        headers.remove(HttpHeaders.CONTENT_LENGTH)
        headers.contentLength = length.toLong()
        headers[HttpHeaders.CONTENT_TYPE] = MediaType.APPLICATION_JSON_VALUE
        newRequest = object : ServerHttpRequestDecorator(newRequest) {
            override fun getHeaders(): HttpHeaders {
                return headers
            }
        }
        exchange.attributes[com.siupay.pci.gateway.constant.PublicConstant.CACHE_REQUEST_BODY_OBJECT_KEY] = newRequestBody
        return chain.filter(exchange.mutate().request(newRequest).build())
    }

    private fun filterXss(requestBody: String?): String {
        val map = objectMapper.readValue(requestBody, JsonNode::class.java)
            return clean(map)?.toJson()!!
    }

    private fun buffer(bytes: ByteArray): DataBuffer {
        val nettyDataBufferFactory = NettyDataBufferFactory(ByteBufAllocator.DEFAULT)
        val buffer: DataBuffer = nettyDataBufferFactory.allocateBuffer(bytes.size)
        buffer.write(bytes)
        return buffer
    }
    override fun getOrder(): Int {
        return Ordered.HIGHEST_PRECEDENCE+10
    }


    private fun clean(node: JsonNode): JsonNode? {
        return if (node.isValueNode) { // Base case - we have a Number, Boolean or String
            if (JsonNodeType.STRING == node.nodeType) {
                // Escape all String values
                JsonNodeFactory.instance.textNode(StringEscapeUtils.escapeHtml4(node.asText()))
            } else {
                node
            }
        } else { // Recursive case - iterate over JSON object entries
            val clean = JsonNodeFactory.instance.objectNode()
            val it = node.fields()
            while (it.hasNext()) {
                val entry = it.next()
                // Encode the key right away and encode the value recursively
                clean.set<JsonNode>(StringEscapeUtils.escapeHtml4(entry.key), clean(entry.value))
            }
            clean
        }
    }

    private fun resolveBodyFromRequest(exchange: ServerWebExchange): String? {
        val gatewayContext: com.siupay.pci.gateway.config.GatewayContext = exchange.getAttribute(com.siupay.pci.gateway.config.GatewayContext.CACHE_GATEWAY_CONTEXT)
        return gatewayContext.cacheBody.toString();
    }

}