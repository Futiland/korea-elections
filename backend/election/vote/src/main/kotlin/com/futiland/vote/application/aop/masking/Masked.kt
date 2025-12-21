package com.futiland.vote.application.aop.masking

/**
 * 마스킹 대상 필드를 표시하는 어노테이션
 * Response DTO의 필드에 적용하여 해당 필드를 마스킹 처리
 *
 * @param type 마스킹 전략 타입 (기본: NAME - 첫 글자만 보이고 나머지 마스킹)
 *
 * 사용 예시:
 * ```
 * data class CreatorInfoResponse(
 *     val accountId: Long,
 *     @Masked(type = MaskingType.NAME)
 *     val name: String,
 * )
 * ```
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Masked(
    val type: MaskingType = MaskingType.NAME
)
