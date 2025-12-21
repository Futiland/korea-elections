package com.futiland.vote.application.aop.masking

import com.futiland.vote.application.common.httpresponse.HttpApiResponse
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

/**
 * 응답 데이터의 마스킹을 처리하는 AOP Aspect
 *
 * - @Masked 어노테이션이 붙은 필드를 자동으로 마스킹 처리
 * - @SkipMasking 어노테이션이 붙은 API는 마스킹 건너뜀
 * - 중첩된 객체와 컬렉션 내부의 객체도 재귀적으로 처리
 */
@Aspect
@Component
class MaskingAspect {

    /**
     * Controller 메서드의 응답을 가로채서 마스킹 처리
     */
    @Around("execution(* com.futiland.vote.application..controller..*(..))")
    fun maskResponse(joinPoint: ProceedingJoinPoint): Any? {
        val result = joinPoint.proceed()

        // @SkipMasking 어노테이션이 있으면 마스킹 건너뜀
        val methodSignature = joinPoint.signature as MethodSignature
        val method = methodSignature.method
        if (method.isAnnotationPresent(SkipMasking::class.java)) {
            return result
        }

        // HttpApiResponse인 경우 data 필드 마스킹
        if (result is HttpApiResponse<*>) {
            result.data?.let { data ->
                applyMasking(data)
            }
        }

        return result
    }

    /**
     * 객체에 마스킹 적용 (재귀적으로 중첩 객체 처리)
     */
    private fun applyMasking(obj: Any) {
        val kClass = obj::class

        kClass.memberProperties.forEach { property ->
            property.isAccessible = true

            val value = (property as KProperty1<Any, *>).get(obj) ?: return@forEach

            // @Masked 어노테이션 확인
            val maskedAnnotation = property.findAnnotation<Masked>()

            if (maskedAnnotation != null && value is String) {
                // 마스킹 적용
                val strategy = maskedAnnotation.type.getStrategy()
                val maskedValue = strategy.mask(value)
                setPropertyValue(obj, property, maskedValue)
            } else {
                // 중첩 객체 또는 컬렉션 처리
                when (value) {
                    is Collection<*> -> {
                        value.filterNotNull().forEach { item ->
                            if (!isPrimitiveOrWrapper(item)) {
                                applyMasking(item)
                            }
                        }
                    }
                    is Array<*> -> {
                        value.filterNotNull().forEach { item ->
                            if (!isPrimitiveOrWrapper(item)) {
                                applyMasking(item)
                            }
                        }
                    }
                    else -> {
                        if (!isPrimitiveOrWrapper(value)) {
                            applyMasking(value)
                        }
                    }
                }
            }
        }
    }

    /**
     * data class의 val 프로퍼티 값 변경
     * Kotlin data class는 기본적으로 val(불변)이므로 리플렉션으로 변경
     */
    private fun setPropertyValue(obj: Any, property: KProperty1<*, *>, newValue: Any) {
        try {
            // Kotlin mutable property인 경우
            if (property is KMutableProperty<*>) {
                property.isAccessible = true
                (property as KMutableProperty<Any?>).setter.call(obj, newValue)
            } else {
                // Java Field를 통한 강제 변경 (val 프로퍼티의 경우)
                val javaField = property.javaField
                if (javaField != null) {
                    javaField.isAccessible = true
                    javaField.set(obj, newValue)
                }
            }
        } catch (e: Exception) {
            // 값 변경 실패 시 무시 (로깅 추가 가능)
        }
    }

    /**
     * 기본 타입 또는 래퍼 타입인지 확인
     */
    private fun isPrimitiveOrWrapper(value: Any): Boolean {
        return value is Number ||
                value is Boolean ||
                value is Char ||
                value is String ||
                value is Enum<*> ||
                value::class.java.isPrimitive ||
                value::class.java.`package`?.name?.startsWith("java.") == true
    }
}
