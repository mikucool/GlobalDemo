package com.example.globaldemo.network.security

import com.example.globaldemo.configuration.ApplicationConfiguration
import com.example.globaldemo.network.interceptor.DefaultRequestInterceptor
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import java.security.KeyFactory
import java.security.SecureRandom
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object HttpSecurityManager {
    private const val AES_ALGORITHM = "AES/CBC/NoPadding"
    private const val RSA_ALGORITHM = "RSA/ECB/PKCS1Padding"
    private const val AES_KEY_LENGTH = 16 // Standard AES key length
    private val CHARSET = Charsets.UTF_8

    /**
     * 使用 AES 加密请求，使用 RSA 加密 AES key
     * @param originalRequest 原始请求
     * @param defaultHttpHeader 默认请求头
     * @return 加密后的请求
     */
    fun encryptRequest(originalRequest: Request, defaultHttpHeader: DefaultRequestInterceptor.DefaultHttpHeader): Request {
        val originalBody = originalRequest.body
        val bodyBuffer = Buffer()
        originalBody?.writeTo(bodyBuffer)
        val originalBodyString = bodyBuffer.readString(CHARSET)
        val newRequest = originalRequest.newBuilder().apply {
            val aesKey = generateRandomKey()
            val defaultHeaderJson = Gson().toJson(defaultHttpHeader)
            val encryptedDefaultHeader = aesEncrypt(defaultHeaderJson, aesKey, aesKey)
            val encryptedBodyString = aesEncrypt(originalBodyString, aesKey, aesKey)
            val encryptedAESKey = rsaEncrypt(aesKey)
            addHeader("headBeanEncrypt", encryptedDefaultHeader)
            addHeader("key", encryptedAESKey)
            addHeader("appId", defaultHttpHeader.appid)
            method(
                originalRequest.method,
                encryptedBodyString.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            )
        }
        return newRequest.build()
    }

    /**
     * 使用 AES 解密响应
     * @param data 待解密数据
     * @param key 解密密钥
     * @param iv 解密偏移量
     */
    fun aesDecryptResponse(
        data: String,
        key: String = ApplicationConfiguration.HTTP_AES_KEY,
        iv: String = ApplicationConfiguration.HTTP_AES_IV
    ): String {
        val cipher = Cipher.getInstance(AES_ALGORITHM)
        val keyBytes = key.toByteArray(CHARSET)
        val ivBytes = iv.toByteArray(CHARSET)
        val secretKeySpec = SecretKeySpec(keyBytes, "AES")
        val ivParameterSpec = IvParameterSpec(ivBytes)
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)
        val encryptedDataBytes = Base64.getDecoder().decode(data)
        val originalDataBytes = cipher.doFinal(encryptedDataBytes)
        return String(originalDataBytes, CHARSET).trim()
    }

    /**
     * 使用 AES 加密请求
     * @param data 待加密数据
     * @param key 加密密钥
     * @param iv 偏移量
     * @return 加密后的数据
     */
    private fun aesEncrypt(data: String, key: String, iv: String): String {
        val cipher = Cipher.getInstance(AES_ALGORITHM)
        val blockSize = cipher.blockSize
        val dataBytes = data.toByteArray(CHARSET)
        var plainTextSize = dataBytes.size
        if (plainTextSize % blockSize != 0) plainTextSize += (blockSize - plainTextSize % blockSize)
        val cipherData = ByteArray(plainTextSize)
        System.arraycopy(dataBytes, 0, cipherData, 0, dataBytes.size)

        val keyBytes = key.toByteArray(CHARSET)
        val ivBytes = iv.toByteArray(CHARSET)
        val secretKeySpec = SecretKeySpec(keyBytes, "AES")
        val ivParameterSpec = IvParameterSpec(ivBytes)

        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)
        val encryptedDataBytes = cipher.doFinal(cipherData)
        return Base64.getEncoder().encodeToString(encryptedDataBytes).trim { it <= ' ' }
    }

    /**
     * 使用 RSA 加密 AES key
     * @param content 待加密数据
     * @param publicKey RSA 公钥
     * @return 加密后的数据
     */
    private fun rsaEncrypt(
        content: String,
        publicKey: String = ApplicationConfiguration.HTTP_RSA_PUBLIC_KEY
    ): String {
        val publicKeyBytes = Base64.getDecoder().decode(publicKey)
        val rsaPublicKey = KeyFactory.getInstance("RSA")
            .generatePublic(X509EncodedKeySpec(publicKeyBytes)) as RSAPublicKey
        val cipher = Cipher.getInstance(RSA_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey)
        val encryptedDataBytes = cipher.doFinal(content.toByteArray(CHARSET))
        return Base64.getEncoder().encodeToString(encryptedDataBytes)
    }

    /**
     * 生成随机 AES key
     * @param length key 长度
     * @return 随机 AES key
     */
    private fun generateRandomKey(length: Int = AES_KEY_LENGTH): String {
        val allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..length)
            .map { allowedChars[SecureRandom().nextInt(allowedChars.length)] }
            .joinToString("")
    }
}