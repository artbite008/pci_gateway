package com.siupay.pci.gateway.utils

import com.siupay.pci.gateway.exception.FormatException
import com.siupay.pci.gateway.util.*
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class UtilsTest {

    @Test
    fun test () {

        var str = "{\"key\":\"value\"}"
        val json = str.toJson()
        println(json)

        JoshuaTest().apply { name = "wang" }.toJson()

        println("wang".toJson())
        println(JoshuaTest().apply { name = "wang" }.toJson())

        Assertions.assertThrows(FormatException::class.java) {
            "{\"sda\"}".fromJson<JoshuaTest>()
        }

        JoshuaTest().apply { name = "wang" }.toJson().fromJson<JoshuaTest>()

        Assertions.assertThrows(FormatException::class.java) {
            "{\"sda\":\"wang\"}".convert<JoshuaTest>()
        }
//        JoshuaTest().apply { name = "wang" }.toJson().convert<JoshuaTest>()

        Assertions.assertThrows(FormatException::class.java) {
            "{\"sda\"}".convertWithSnakeCase<JoshuaTest>()
        }
//        "{\"name\":\"wang\"}".convertWithSnakeCase<JoshuaTest>()

        println(GsonUtils.toJson(JoshuaTest().apply { name = "wang" }))
        val obj = GsonUtils.toObject("{\"name\":\"wang\"}", JoshuaTest::class.java)

    }
}

 class JoshuaTest{
     var name: String? = null
 }

