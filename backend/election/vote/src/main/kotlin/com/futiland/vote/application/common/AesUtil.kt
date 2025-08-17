package com.futiland.vote.application.common

import com.futiland.vote.application.common.httpresponse.CodeEnum
import com.futiland.vote.application.exception.ApplicationException
import com.futiland.vote.domain.common.TextEncryptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


@Component
class AesUtil(
    @Value("\${password.secret-key}")
    private val secretKey: String,
    @Value("AES")
    private val algorithm: String = "AES"
) : TextEncryptor {
    init {
        validateSecretKey(secretKey)
    }

    override fun encrypt(plainText: String): String {
        val keySpec = SecretKeySpec(secretKey.toByteArray(), algorithm)
        val cipher: Cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        val encrypted: ByteArray = cipher.doFinal(plainText.toByteArray())
        return Base64.getUrlEncoder().encodeToString(encrypted) // URL-safe
    }

    override fun decrypt(cipherText: String): String {
        val keySpec = SecretKeySpec(secretKey.toByteArray(), algorithm)
        val cipher: Cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.DECRYPT_MODE, keySpec)
        val decoded: ByteArray = Base64.getUrlDecoder().decode(cipherText)
        val decrypted: ByteArray = cipher.doFinal(decoded)
        return String(decrypted)
    }

    override fun validateSecretKey(secretKey: String) {
        val validLengths = setOf(16, 24, 32)
        if (secretKey.length !in validLengths) {
            throw ApplicationException(
                code = CodeEnum.FRS_001,
                message = "AES 암호화 키는 16, 24, 32자만 가능합니다. 현재 키 길이: ${secretKey.length}",
                data = mapOf("secretKey" to secretKey)
            )
        }
    }

    override fun isPlainText(text: String): Boolean {
        // AES 암호문은 Base64 URL-safe 인코딩이므로, 해당 패턴이 아니면 평문으로 간주
        val base64UrlSafeRegex = Regex("^[A-Za-z0-9_-]+={0,2}$")
        // 길이 체크(암호문은 최소 16바이트 이상, Base64 인코딩 시 22자 이상)
        if (text.length >= 22 && base64UrlSafeRegex.matches(text)) {
            return false // 암호문
        }
        return true // 평문
    }

    private fun containsPlainPhoneNumber(text: String): Boolean {
        // 010-1234-5678, 01012345678 등 패턴
        val phoneRegex = Regex("""01[016789]-?\d{3,4}-?\d{4}""")
        return phoneRegex.containsMatchIn(text)
    }

    private fun containsKoreanPlainName(text: String): Boolean {
        // 한글 2~5자 또는 영문 2~20자 (공백 포함)
        val koreanNameRegex = Regex("^[가-힣]{2,6}$")
        return koreanNameRegex.matches(text)
    }
}