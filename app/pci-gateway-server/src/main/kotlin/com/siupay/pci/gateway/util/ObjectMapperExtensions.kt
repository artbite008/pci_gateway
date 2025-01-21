package com.siupay.pci.gateway.util

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.vavr.jackson.datatype.VavrModule
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.*

fun baseObjectMapper(): ObjectMapper =
    jacksonObjectMapper()
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
        .configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true)
        .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL,true)
        .registerModule(Jdk8Module())
        .registerJavaTimeModule()
        .registerVavrModule()

fun ignoreNullObjectMapper(): ObjectMapper =
    baseObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL)

fun snakeCaseObjectMapper(): ObjectMapper =
    ignoreNullObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)

fun ObjectMapper.registerVavrModule(): ObjectMapper =
    this.registerModule(VavrModule(VavrModule.Settings().useOptionInPlainFormat(false)))

fun ObjectMapper.registerJavaTimeModule(): ObjectMapper =
    this.registerModule(
        JavaTimeModule()
            .addSerializer(LocalDate::class.java, LocalDateSerializer(ofPattern("yyyy-MM-dd")))
            .addDeserializer(LocalDate::class.java, LocalDateDeserializer(ofPattern("yyyy-MM-dd")))
            .addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer(ofPattern("yyyy-MM-dd HH:mm:ss")))
            .addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer(ofPattern("yyyy-MM-dd HH:mm:ss")))
    )