package com.siupay.pci.gateway.service

import com.siupay.pci.gateway.config.properties.BindCardRsaProperties
import org.springframework.stereotype.Service
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher

//PA-2558: PCI security
@Service
class RSAService(
    val bindCardRsaProperties: com.siupay.pci.gateway.config.properties.BindCardRsaProperties
) {

    private val encryptType = "RSA/ECB/PKCS1Padding"

    fun encryptByPrivateKey(input: String, privateKey: PrivateKey): String {

        //1 创建cipher对象
        val cipher = Cipher.getInstance(encryptType)

        //2 初始化cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, privateKey)

        //3 加密或解密
        val encrypt = cipher.doFinal(input.toByteArray())

        return Base64.getEncoder().encodeToString(encrypt)
    }

    fun decryptByPrivateKey(input: String, privateKey: PrivateKey): String {
        var inputByteArray = Base64.getDecoder().decode(input)

        //1 创建cipher对象
        val cipher = Cipher.getInstance(encryptType)

        //2 初始化cipher对象
        cipher.init(Cipher.DECRYPT_MODE, privateKey)

        //3 加密或解密
        val decrypt = cipher.doFinal(inputByteArray)

        return String(decrypt)
    }

    fun decryptByPublicKey(input: String, publicKey: PublicKey): String {
        var inputByteArray = Base64.getDecoder().decode(input)

        //1 创建cipher对象
        val cipher = Cipher.getInstance(encryptType)

        //2 初始化cipher对象
        cipher.init(Cipher.DECRYPT_MODE, publicKey)

        //3 加密或解密
        val decrypt = cipher.doFinal(inputByteArray)

        return String(decrypt)
    }

    fun getPublicKey(): PublicKey {
        val key = bindCardRsaProperties.rsaPublicKey.replace("\\s","").replace("\\n","")
        val keyArray = Base64.getMimeDecoder().decode(key.toByteArray())
        val spec = X509EncodedKeySpec(keyArray)
        val fact = KeyFactory.getInstance("RSA")
        return fact.generatePublic(spec)
    }

    fun getPrivateKey(): PrivateKey {
        val clear: ByteArray = Base64.getMimeDecoder().decode(bindCardRsaProperties.rsaPrivateKey.replace("\\s","").replace("\\n","").toByteArray())
        val keySpec = PKCS8EncodedKeySpec(clear)
        val fact = KeyFactory.getInstance("RSA")
        val priv = fact.generatePrivate(keySpec)
        Arrays.fill(clear, 0.toByte())
        return priv
    }

    fun generatePair(): Pair<PublicKey, PrivateKey> {
        //如何生成秘钥对
        val generator = KeyPairGenerator.getInstance("RSA")//秘钥生成器
        val keyPair = generator.genKeyPair()//生成秘钥对
        val publicKey = keyPair.public//公钥
        val privateKey = keyPair.private//私钥
        return Pair(publicKey, privateKey)
    }
}