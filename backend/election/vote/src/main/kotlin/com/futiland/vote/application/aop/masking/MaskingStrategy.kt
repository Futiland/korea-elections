package com.futiland.vote.application.aop.masking

/**
 * 마스킹 전략 인터페이스
 * 다양한 마스킹 방식을 구현할 수 있도록 전략 패턴 적용
 */
interface MaskingStrategy {
    /**
     * 주어진 값을 마스킹 처리
     * @param value 원본 값
     * @return 마스킹된 값
     */
    fun mask(value: String): String
}

/**
 * 이름 마스킹 전략
 * 첫 글자만 보이고 나머지는 * 처리
 * 예: "홍길동" -> "홍**"
 */
class NameMaskingStrategy : MaskingStrategy {
    override fun mask(value: String): String {
        if (value.isEmpty()) return value
        if (value.length == 1) return value

        val firstChar = value.first()
        val maskedPart = "*".repeat(value.length - 1)
        return "$firstChar$maskedPart"
    }
}

/**
 * 중간 글자 마스킹 전략
 * 첫 글자와 마지막 글자만 보이고 중간은 * 처리
 * 예: "홍길동" -> "홍*동"
 */
class MiddleMaskingStrategy : MaskingStrategy {
    override fun mask(value: String): String {
        if (value.isEmpty()) return value
        if (value.length <= 2) return value

        val firstChar = value.first()
        val lastChar = value.last()
        val maskedPart = "*".repeat(value.length - 2)
        return "$firstChar$maskedPart$lastChar"
    }
}

/**
 * 전체 마스킹 전략
 * 모든 글자를 * 처리
 */
class FullMaskingStrategy : MaskingStrategy {
    override fun mask(value: String): String {
        return "*".repeat(value.length)
    }
}

/**
 * 마스킹 전략 타입
 */
enum class MaskingType {
    /**
     * 첫 글자만 보이고 나머지 마스킹
     * 예: "홍길동" -> "홍**"
     */
    NAME,

    /**
     * 첫 글자와 마지막 글자만 보이고 중간 마스킹
     * 예: "홍길동" -> "홍*동"
     */
    MIDDLE,

    /**
     * 전체 마스킹
     */
    FULL;

    fun getStrategy(): MaskingStrategy {
        return when (this) {
            NAME -> NameMaskingStrategy()
            MIDDLE -> MiddleMaskingStrategy()
            FULL -> FullMaskingStrategy()
        }
    }
}
