package com.futiland.vote.application.common

import com.futiland.vote.application.exception.ApplicationException
import com.futiland.vote.domain.common.TextEncryptor
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class AesUtilTest {
    private lateinit var textEncryptor: TextEncryptor

    @BeforeEach
    fun setUp() {
        this.textEncryptor = AesUtil(
            secretKey = "tessfghhhhhhhhhh",
        )
    }

    @Test
    fun `시크릿키가 조건에 만족(길이등)에 만족하지 않으면 예외 발생`(){
        // Arrange
        val invalidSecretKey = "short"

        // Action, Assert
        assertThatThrownBy { AesUtil(
            secretKey = invalidSecretKey,
            algorithm = "AES"
        ) }
            .isInstanceOf(ApplicationException::class.java)
    }

    @Test
    fun `특정 문자 암호화 하고 복호화 하기`(){
        // Arrange
        val plainText = "test1234!@#$%^"
        // Action
        val encryptText = textEncryptor.encrypt(plainText)
        val cihpher = textEncryptor.decrypt(encryptText)
        // Assert
        assertThat(cihpher).isEqualTo(plainText)
    }

    @Test
    fun `전화번호가 노출된경우 평문인지 확인`(){
        // Arrange
        val phoneNumber = "01012345678"
        // Action
        val isPlainText = textEncryptor.isPlainText(phoneNumber)
        // Assert
        assertThat(isPlainText).isTrue()
    }

    @Test
    fun `이름이 노출된 경우 평문인지 확인`(){
        // Arrange
        val name= "홍길동"
        // Action
        val isPlainText = textEncryptor.isPlainText(name)
        // Assert
        assertThat(isPlainText).isTrue()
    }

    @Test
    fun `암호화된 문자열이 평문인지 확인`() {
        // Arrange
        val encryptedText = textEncryptor.encrypt("암호화된 문자열입니다.")
        // Action
        val isPlainText = textEncryptor.isPlainText(encryptedText)
        // Assert
        assertThat(isPlainText).isFalse()
    }

}