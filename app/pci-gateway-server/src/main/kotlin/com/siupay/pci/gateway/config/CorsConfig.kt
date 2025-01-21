package com.siupay.pci.gateway.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter


@Configuration
class CorsConfig {
    @Bean
    fun corsFilter(): CorsFilter? {
        val corsConfiguration = CorsConfiguration()
        //1,允许任何来源
        corsConfiguration.allowedOriginPatterns = listOf("*")
        //2,允许任何请求头
        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL)
        //3,允许任何方法
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL)
        //4,允许凭证
        corsConfiguration.allowCredentials = true
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", corsConfiguration)
        return CorsFilter(source)
    }
}