package com.futiland.vote.application.aop.masking

/**
 * 마스킹 처리를 건너뛰는 API를 표시하는 어노테이션
 * Controller 메서드에 적용하면 해당 API 응답에서는 마스킹이 적용되지 않음
 *
 * 사용 예시:
 * ```
 * @SkipMasking
 * @GetMapping("/info/profile")
 * fun getAccountInfo(...): HttpApiResponse<ProfileResponse>
 * ```
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class SkipMasking
