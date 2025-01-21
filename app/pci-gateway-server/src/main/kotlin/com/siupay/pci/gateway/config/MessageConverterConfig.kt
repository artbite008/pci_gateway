package com.siupay.pci.gateway.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.google.common.collect.Lists
import io.vavr.jackson.datatype.VavrModule
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.http.HttpMessageConverters
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import java.util.function.Consumer


@Configuration
class MessageConverterConfig {
    @Bean
    @ConditionalOnMissingBean
    fun messageConverters(converters: ObjectProvider<HttpMessageConverter<*>>): HttpMessageConverters {
        /** 当不存在HttpMessageConverters 时从对象工厂获取HttpMessageConverter构建改对象用于处理http请求,http请求是基于文本,所以需要该对象进行文本请求/响应转化 **/
        val converterList: MutableList<HttpMessageConverter<*>> = Lists.newArrayList()
        converters.forEach(Consumer { httpMessageConverter: HttpMessageConverter<*> -> converterList.add(httpMessageConverter) })
        return HttpMessageConverters(converterList)
    }

    @Bean
    @ConditionalOnMissingBean(ObjectMapper::class)
    fun jacksonObjectMapper(builder: Jackson2ObjectMapperBuilder): ObjectMapper? {
        /** 设置Jackson序列化配置,将Json转化为对象,json中出现未知对象不报错,空对象报错,不区分字段大小写 **/
        return builder.createXmlMapper(false)
                .build<ObjectMapper>()
                .registerModule(VavrModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, true)
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
    }
}