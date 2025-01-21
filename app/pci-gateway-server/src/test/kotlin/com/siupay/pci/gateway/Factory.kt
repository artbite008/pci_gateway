package com.siupay.pci.gateway

import com.siupay.pci.gateway.dto.*
import com.siupay.pci.tokenservice.dto.*
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

fun cardTokenRequest(): CardTokenRequest {
    return CardTokenRequest(
        requestId = "requestId",
        expiryYear = "2099",
        expiryMonth = "12",
        cardNumber = "6214830065431212",
        cvv = "123"
    )
}

fun bindCardRequest(): com.siupay.pci.gateway.dto.BindCardRequest {
    return com.siupay.pci.gateway.dto.BindCardRequest(
            requestId = "requestId",
            expiryYear = "2099",
            expiryMonth = "12",
            cardNumber = "6214830065431212",
            cvv = "123",
            sessionId = null
    )
}

fun bindCardResponse(): com.siupay.pci.gateway.dto.BindCardResponse {
    return com.siupay.pci.gateway.dto.BindCardResponse(
            token = "token",
            first6Last4 = "6222222***1234",
            expiryYear = "2099",
            expiryMonth = "12",
            requestId = "requestId",
            credential = "credential"
    )
}

fun cardInfoRequest(): CardInfoRequest {
    return CardInfoRequest(
        token = "token"
    )
}

fun cardTokenResponse(): CardTokenResponse {
    return CardTokenResponse(
        token = "token",
        first6Last4 = "6222222***1234",
        expiryYear = "2099",
        expiryMonth = "12",
        requestId = "requestId"
    )
}

fun cardInfoResponse(): CardInfoResponse {
    return CardInfoResponse(
        cardToken = "cardToken",
        cardNumber = "cardNumber",
        expiryYear = "expiryYear",
        expiryMonth = "expiryMonth",
        cvv = "cvv",
        uid = "externalId"
    )
}

fun cardTokenInfoResponse(): CardTokenInfoResponse {
    return CardTokenInfoResponse(
        cardToken = "cardToken",
        expiryYear = "expiryYear",
        expiryMonth = "expiryMonth",
        first6Last4 = "6222222***1234",
        uid = "externalId"
    )
}

fun pciChannelDispatchRequest(): com.siupay.pci.gateway.dto.PciChannelDispatchRequest {
    return com.siupay.pci.gateway.dto.PciChannelDispatchRequest().apply {
        additionalData = com.siupay.pci.gateway.dto.AdditionalData().apply {
            tokenKey = "4b66ebcf-8e5c-4a24-881f-db90d65f61f6"
            channelId = "checkout"
            transactionType = "auth"
            channelUrl = "https://api.sandbox.checkout.com/payments"
        }

        header = mapOf(
            "Cko-Idempotency-Key" to "123ww11556660999oiiie77",
            "Authorization" to "sk_test_e74e8750-03d8-4f50-bd21-4ac4edb2b3bd",
            "Content-Type" to "application/json"
        )

        body =
            "{\\\"source\\\":{\\\"type\\\":\\\"card\\\",\\\"number\\\":\\\"@@cardNo@@\\\",\\\"expiry_month\\\":@@expireMonth@@,\\\"expiry_year\\\":@@expireYear@@},\\\"amount\\\":6540,\\\"currency\\\":\\\"USD\\\",\\\"payment_type\\\":\\\"Recurring\\\",\\\"reference\\\":\\\"82fe10a4-3d94-4bb6-b5c4-9274aa1ce07b\\\",\\\"description\\\":\\\"Set of 3 masks\\\",\\\"capture\\\":false,\\\"capture_on\\\":\\\"2019-09-10T10:11:12Z\\\",\\\"customer\\\":{\\\"id\\\":\\\"cus_c6vkcd6vdvsulnmlvtfvcj5v4e\\\",\\\"email\\\":\\\"JohnTest@test.com\\\",\\\"name\\\":\\\"John Test\\\"},\\\"billing_descriptor\\\":{\\\"name\\\":\\\"SUPERHEROES.COM\\\",\\\"city\\\":\\\"GOTHAM\\\"},\\\"shipping\\\":{\\\"address\\\":{\\\"address_line1\\\":\\\"Checkout.com\\\",\\\"address_line2\\\":\\\"90 Tottenham Court Road\\\",\\\"city\\\":\\\"London\\\",\\\"state\\\":\\\"London\\\",\\\"zip\\\":\\\"W1T 4TJ\\\",\\\"country\\\":\\\"GB\\\"},\\\"phone\\\":{\\\"country_code\\\":\\\"+1\\\",\\\"number\\\":\\\"415 555 2671\\\"}},\\\"3ds\\\":{\\\"enabled\\\":true,\\\"attempt_n3d\\\":false,\\\"eci\\\":\\\"05\\\",\\\"cryptogram\\\":\\\"AgAAAAAAAIR8CQrXcIhbQAAAAAA=\\\",\\\"xid\\\":\\\"MDAwMDAwMDAwMDAwMDAwMzIyNzY=\\\",\\\"version\\\":\\\"2.0.1\\\"},\\\"risk\\\":{\\\"enabled\\\":false},\\\"success_url\\\":\\\"https://enuhssrdx60twze.m.pipedream.net\\\",\\\"failure_url\\\":\\\"https://enuhssrdx60twze.m.pipedream.net\\\",\\\"payment_ip\\\":\\\"90.197.169.245\\\",\\\"recipient\\\":{\\\"dob\\\":\\\"1985-05-15\\\",\\\"account_number\\\":\\\"5555554444\\\",\\\"zip\\\":\\\"W1T\\\",\\\"last_name\\\":\\\"Jones\\\"},\\\"metadata\\\":{\\\"coupon_code\\\":\\\"NY2018\\\",\\\"partner_id\\\":123989}}"
    }
}

fun pciChannelDispatchResponse(): com.siupay.pci.gateway.dto.PciChannelDispatchResponse {
    return com.siupay.pci.gateway.dto.PciChannelDispatchResponse().apply {
        code = "0000"
        message = ""
        channelResponse = emptyMap()
        data = ""
    }
}

fun cardTokenRequestValidateRequest() = CardTokenRequest(
    cardNumber = "4543474002249996",
    cvv = "956",
    requestId = UUID.randomUUID().toString(),
    expiryMonth = "10",
    expiryYear = "29"
)

fun getPrivateKey(): PrivateKey {
    val clear: ByteArray = Base64.getMimeDecoder().decode(
        "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDJxWOAv+yhSAN//k7t3c2tft9CNrcbM7Fqf9v7hfuxgx1V3XuXQaoDhXzg/NQtDC3Lgmpa3quy65yvMghBY8u7S2E69OzE0DI4C0o4Cyv0PMTXsC4C9wRaN1m8O8brNL382yb0Dr49A7BL5/wS7rSa466moJ3BXp0FPfxV6UgHv+Xdt+tYbHQc102B9yOzHsUs8qaxKUU+yOYLn1d5aiUaPsApBhcRieoImH6FxSbaRzLBiagjVk42v0cs48CXgTv2NFxeg5CGt4feKOOTspU4tHZkyV5nycmNHJWR64MKE09RcgVlOg7H8Uad551XDvCf/Sd5DAGyIcf8VlYCUgOnAgMBAAECggEAWeMBrP3TYRY/2OlmKDnDwyLeOxU9BXVF+Hf1fFv4AOC0VOhSgtPR9bemJmT78QWX8S2sS8cfHpowNyxG2TqoLEwsiKNMR3Gorab57XWsoyXfW2c1WETLP5Wg+GZ4+5eHe/n1pIxc27mk9GvotFZ7RvVffQPYw6hus0MmAu/mEuzz6J/CmE1SoA2w5SKevSiiDebUOTwhn4Ifx+m5yX7IMMsxpnMNNlz5G8Umc0PfJGDygy+GrwLCER9j/NOla+SjvnLK9iMDu8SJ3EfiYmJl1LIQjP7TYCf2cP0BeGRCmLVRmhDt4cAscXNduvlbyy/HtCPBCeOHBohVhmhYdtrpgQKBgQDm0IcgNpRwaPWGp0pXLG6ubrkyi78e0EN/XmYNMNAxNsF3fH7DZwqGfOu4ZEmC7BPhUESiUWNCxmcYe8QCGV/fPIjXaZyapbU5Iamzj+idY6zPtOqo2wbkbw83cH813x0BR/Jz9VnQJcevhkUPKzxqwS31b89SrPEw1bFVsXbdMQKBgQDfyZJ6vmBshlGm+5zyaD/ua+9KRKB0H0rwrBaWeDnWSl1hfc4JnSaNhZZgsfbv3Km8PU7lorpkUM0Adp/8yXMfChIhp+8du55Vmn7ZOTvZZX9qXoFgObaqrUvhv5akzrhYWqK/QRrwKQVd3gR9pZm/vfNPhLCpvcBJBMhLfCRYVwKBgQCBlMaX3ix9418cRqUkxawpv723U99rdC7AvgV7GFF5n60D4N/l4d513IIV1i+zEcijcQXnPtwELBDLHQjQyOAbGwgUL5Wc+LUCKxTz2zIT0la9kWtAex6mdndLf5vbabQF+Rdz5GWUxvERaMcymepkYKnlpez2FGOgMAhL/MKnsQKBgQCl9bzVnj2v/eSr60k3VCylShJJh59evp464cRWjo6F5txdooicBJlEtt8QkqpIs+KEoQrTrdyA7JM7Vr3LIDJpwHQ11W61JCCF529O9Oz3ihf18GQN0n8vEBmILycuzcdUbtLm9wddN26tcCrwa1EcK2g3fnXtS7U/8XYA1O23PQKBgQC3Kw1smRmFj1QVtdkPMuhGNRhCAHypBg+weEJb/kd1x193vwBisZipXOhhXcA64hHIxLk7QzPHbF2QEOkDo4Eba36BFKztnj89VDx3gm/j+tETxsalJlg7gmQOjecaTpLkspKwcsKCawMw9UCws7fhEdW/HghPm4bBvATqeEZ72g==".replace(
            "\\s",
            ""
        ).replace("\\n", "").toByteArray()
    )
    val keySpec = PKCS8EncodedKeySpec(clear)
    val fact = KeyFactory.getInstance("RSA")
    val priv = fact.generatePrivate(keySpec)
    Arrays.fill(clear, 0.toByte())
    return priv
}

fun getPublicKey(): PublicKey {
    val key =
        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAycVjgL/soUgDf/5O7d3NrX7fQja3GzOxan/b+4X7sYMdVd17l0GqA4V84PzULQwty4JqWt6rsuucrzIIQWPLu0thOvTsxNAyOAtKOAsr9DzE17AuAvcEWjdZvDvG6zS9/Nsm9A6+PQOwS+f8Eu60muOupqCdwV6dBT38VelIB7/l3bfrWGx0HNdNgfcjsx7FLPKmsSlFPsjmC59XeWolGj7AKQYXEYnqCJh+hcUm2kcywYmoI1ZONr9HLOPAl4E79jRcXoOQhreH3ijjk7KVOLR2ZMleZ8nJjRyVkeuDChNPUXIFZToOx/FGneedVw7wn/0neQwBsiHH/FZWAlIDpwIDAQAB".replace(
            "\\s",
            ""
        ).replace("\\n", "")
    val keyArray = Base64.getMimeDecoder().decode(key.toByteArray())
    val spec = X509EncodedKeySpec(keyArray)
    val fact = KeyFactory.getInstance("RSA")
    return fact.generatePublic(spec)
}