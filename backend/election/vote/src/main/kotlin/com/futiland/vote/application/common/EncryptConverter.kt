package com.futiland.vote.application.common

import com.futiland.vote.domain.common.TextEncryptor
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter


@Converter
class EncryptConverter(
    private val encryptor: TextEncryptor // Spring Bean 주입 필요
) : AttributeConverter<String, String> {
    override fun convertToDatabaseColumn(attribute: String?): String? {
        return attribute?.let { encryptor.encrypt(it) }
    }
    override fun convertToEntityAttribute(dbData: String?): String? {
        return dbData?.let {
            if (encryptor.isPlainText(it)) it else encryptor.decrypt(it)
        }
    }
}