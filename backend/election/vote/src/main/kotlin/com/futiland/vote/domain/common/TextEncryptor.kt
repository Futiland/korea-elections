package com.futiland.vote.domain.common

interface TextEncryptor {
    /**
     * 주어진 평문 문자열을 암호화하여 반환합니다.
     * @param plainText 암호화할 문자열
     * @return 암호화된 문자열 (예: Base64 인코딩된 문자열)
     */
    fun encrypt(plainText: String): String

    /**
     * 암호화된 문자열을 복호화하여 평문으로 반환합니다.
     * @param cipherText 복호화할 문자열 (예: Base64 인코딩된 암호문)
     * @return 복호화된 평문 문자열
     */
    fun decrypt(cipherText: String): String

    /**
     * 암호화 키의 유효성을 검사합니다.
     * @param secretKey 검사할 암호화 키
     * @throws ApplicationException 암호화 키가 유효하지 않은 경우
     */
    fun validateSecretKey(secretKey: String)

    fun isPlainText(text: String): Boolean
}