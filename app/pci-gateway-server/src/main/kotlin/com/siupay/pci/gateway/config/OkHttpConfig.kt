package com.siupay.pci.gateway.config

import com.siupay.pci.gateway.config.properties.OkHttpProperties
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.InetSocketAddress
import java.net.Proxy
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


@Configuration
@EnableConfigurationProperties(com.siupay.pci.gateway.config.properties.OkHttpProperties::class)
class OkHttpConfig {
    @Bean
    fun x509TrustManager(): X509TrustManager {
        return object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(x509Certificates: Array<X509Certificate>, s: String) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(x509Certificates: Array<X509Certificate>, s: String) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate?> {
                return arrayOfNulls(0)
            }
        }
    }

    @Bean
    fun sslSocketFactory(x509TrustManager: X509TrustManager): SSLSocketFactory? {
        try {
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, arrayOf<TrustManager>(x509TrustManager), SecureRandom())
            return sslContext.socketFactory
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        }
        return null
    }

    @Bean
    fun pool(okHttpProperties: com.siupay.pci.gateway.config.properties.OkHttpProperties): ConnectionPool {
        return ConnectionPool(okHttpProperties.maxIdleConnections!!, okHttpProperties.keepaliveDuration!!.toLong(), TimeUnit.SECONDS)
    }

    @Bean
    fun okHttpClient(okHttpProperties: com.siupay.pci.gateway.config.properties.OkHttpProperties, sslSocketFactory: SSLSocketFactory?, x509TrustManager: X509TrustManager?, connectionPool: ConnectionPool?): OkHttpClient {
        val builder = OkHttpClient.Builder()
        return builder.sslSocketFactory(sslSocketFactory, x509TrustManager)
                    .retryOnConnectionFailure(false)
                    .connectionPool(connectionPool)
                    .connectTimeout(okHttpProperties.connectTimeout!!.toLong(), TimeUnit.SECONDS)
                    .readTimeout(okHttpProperties.readTimeout!!.toLong(), TimeUnit.SECONDS)
                    .build()
    }
}