package com.siupay.pci.gateway.service

import com.siupay.pci.gateway.config.properties.BindCardRsaProperties
import com.siupay.pci.gateway.getPrivateKey
import com.siupay.pci.gateway.getPublicKey
import com.siupay.pci.gateway.util.toJson
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class RSAServiceTest {
    @RelaxedMockK
    lateinit var properties: com.siupay.pci.gateway.config.properties.BindCardRsaProperties

    @InjectMockKs
    lateinit var target: RSAService

    @BeforeEach
    fun before() {
        every { properties.rsaPublicKey} returns "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAycVjgL/soUgDf/5O7d3NrX7fQja3GzOxan/b+4X7sYMdVd17l0GqA4V84PzULQwty4JqWt6rsuucrzIIQWPLu0thOvTsxNAyOAtKOAsr9DzE17AuAvcEWjdZvDvG6zS9/Nsm9A6+PQOwS+f8Eu60muOupqCdwV6dBT38VelIB7/l3bfrWGx0HNdNgfcjsx7FLPKmsSlFPsjmC59XeWolGj7AKQYXEYnqCJh+hcUm2kcywYmoI1ZONr9HLOPAl4E79jRcXoOQhreH3ijjk7KVOLR2ZMleZ8nJjRyVkeuDChNPUXIFZToOx/FGneedVw7wn/0neQwBsiHH/FZWAlIDpwIDAQAB"
        every { properties.rsaPublicKey} returns "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDJxWOAv+yhSAN//k7t3c2tft9CNrcbM7Fqf9v7hfuxgx1V3XuXQaoDhXzg/NQtDC3Lgmpa3quy65yvMghBY8u7S2E69OzE0DI4C0o4Cyv0PMTXsC4C9wRaN1m8O8brNL382yb0Dr49A7BL5/wS7rSa466moJ3BXp0FPfxV6UgHv+Xdt+tYbHQc102B9yOzHsUs8qaxKUU+yOYLn1d5aiUaPsApBhcRieoImH6FxSbaRzLBiagjVk42v0cs48CXgTv2NFxeg5CGt4feKOOTspU4tHZkyV5nycmNHJWR64MKE09RcgVlOg7H8Uad551XDvCf/Sd5DAGyIcf8VlYCUgOnAgMBAAECggEAWeMBrP3TYRY/2OlmKDnDwyLeOxU9BXVF+Hf1fFv4AOC0VOhSgtPR9bemJmT78QWX8S2sS8cfHpowNyxG2TqoLEwsiKNMR3Gorab57XWsoyXfW2c1WETLP5Wg+GZ4+5eHe/n1pIxc27mk9GvotFZ7RvVffQPYw6hus0MmAu/mEuzz6J/CmE1SoA2w5SKevSiiDebUOTwhn4Ifx+m5yX7IMMsxpnMNNlz5G8Umc0PfJGDygy+GrwLCER9j/NOla+SjvnLK9iMDu8SJ3EfiYmJl1LIQjP7TYCf2cP0BeGRCmLVRmhDt4cAscXNduvlbyy/HtCPBCeOHBohVhmhYdtrpgQKBgQDm0IcgNpRwaPWGp0pXLG6ubrkyi78e0EN/XmYNMNAxNsF3fH7DZwqGfOu4ZEmC7BPhUESiUWNCxmcYe8QCGV/fPIjXaZyapbU5Iamzj+idY6zPtOqo2wbkbw83cH813x0BR/Jz9VnQJcevhkUPKzxqwS31b89SrPEw1bFVsXbdMQKBgQDfyZJ6vmBshlGm+5zyaD/ua+9KRKB0H0rwrBaWeDnWSl1hfc4JnSaNhZZgsfbv3Km8PU7lorpkUM0Adp/8yXMfChIhp+8du55Vmn7ZOTvZZX9qXoFgObaqrUvhv5akzrhYWqK/QRrwKQVd3gR9pZm/vfNPhLCpvcBJBMhLfCRYVwKBgQCBlMaX3ix9418cRqUkxawpv723U99rdC7AvgV7GFF5n60D4N/l4d513IIV1i+zEcijcQXnPtwELBDLHQjQyOAbGwgUL5Wc+LUCKxTz2zIT0la9kWtAex6mdndLf5vbabQF+Rdz5GWUxvERaMcymepkYKnlpez2FGOgMAhL/MKnsQKBgQCl9bzVnj2v/eSr60k3VCylShJJh59evp464cRWjo6F5txdooicBJlEtt8QkqpIs+KEoQrTrdyA7JM7Vr3LIDJpwHQ11W61JCCF529O9Oz3ihf18GQN0n8vEBmILycuzcdUbtLm9wddN26tcCrwa1EcK2g3fnXtS7U/8XYA1O23PQKBgQC3Kw1smRmFj1QVtdkPMuhGNRhCAHypBg+weEJb/kd1x193vwBisZipXOhhXcA64hHIxLk7QzPHbF2QEOkDo4Eba36BFKztnj89VDx3gm/j+tETxsalJlg7gmQOjecaTpLkspKwcsKCawMw9UCws7fhEdW/HghPm4bBvATqeEZ72g=="
    }

    @Test
    fun generatePair() {
        val pair = target.generatePair()
        println(pair.toJson())
    }

    @Test
    fun testPrivateEncryptAndPublicKeyCheckSign() {
        val text = "wang"
        val privateText = target.encryptByPrivateKey(text, getPrivateKey())
        val publicText = target.decryptByPublicKey(privateText, getPublicKey())
        println("text:"+text)
        println("publicText:"+publicText)
    }
}