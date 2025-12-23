package com.futiland.vote.application.config

import com.futiland.vote.application.common.httpresponse.CodeEnum
import com.futiland.vote.application.common.httpresponse.HttpApiResponse
import com.futiland.vote.application.exception.ApplicationException
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.stream.Collectors

val logger = KotlinLogging.logger {}

@RestControllerAdvice
class ControllerExceptionHandler {

    @ExceptionHandler(ApplicationException::class)
    fun handleApplicationException(e: ApplicationException): ResponseEntity<HttpApiResponse<*>> {
        logger.error { "Application Exception occurred. code=${e.code.name}, message=${e.message}" }
        logger.error { e.stackTraceToString() }

        if(e.code == CodeEnum.FRS_002) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(
                    HttpApiResponse.fromExceptionMessage(
                        code = e.code,
                        message = e.message ?: e.code.description,
                        data = e.data ?: emptyMap()
                    )
                )
        }

        if(e.code == CodeEnum.FRS_001) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(
                    HttpApiResponse.fromExceptionMessage(
                        code = e.code,
                        message = e.message ?: e.code.description,
                        data = e.data ?: emptyMap()
                    )
                )
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                HttpApiResponse.fromExceptionMessage(
                    code = e.code,
                    message = e.message ?: "",
                    data = e.data ?: emptyMap()
                )
            )
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingServletRequestParameterException(e: MissingServletRequestParameterException): ResponseEntity<HttpApiResponse<*>> {
        logger.error { "MissingServletRequestParameter Exception occurred. parameterName=${e.parameterName}, message=${e.message}" }
        logger.error { e.stackTraceToString() }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                HttpApiResponse.fromExceptionMessage(
                    code = CodeEnum.FRS_003,
                    message = e.message ?: ""
                )
            )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<HttpApiResponse<*>> {
        logger.error { "MethodArgumentNotValidException Exception occurred. message=${e.message}" }
        logger.error { e.stackTraceToString() }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                HttpApiResponse.fromExceptionMessage(
                    code = CodeEnum.FRS_003,
                    message = createMessage(e)
                )
            )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(e: HttpMessageNotReadableException): ResponseEntity<HttpApiResponse<*>> {
        logger.error { "HttpMessageNotReadableException Exception occurred. message=${e.message}" }
        logger.error { e.stackTraceToString() }

        val message = extractMissingFieldMessage(e) ?: "요청 본문을 파싱할 수 없습니다"

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                HttpApiResponse.fromExceptionMessage(
                    code = CodeEnum.FRS_003,
                    message = message
                )
            )
    }

    private fun extractMissingFieldMessage(e: HttpMessageNotReadableException): String? {
        val cause = e.cause
        if (cause is com.fasterxml.jackson.databind.exc.MismatchedInputException) {
            val fieldName = cause.path.lastOrNull()?.fieldName ?: return null
            return "${fieldName}은(는) 필수 입력 항목입니다"
        }
        if (cause is com.fasterxml.jackson.databind.exc.ValueInstantiationException) {
            val message = cause.message ?: return null
            val regex = """parameter (\w+)""".toRegex()
            val match = regex.find(message)
            if (match != null) {
                val fieldName = match.groupValues[1]
                return "${fieldName}은(는) 필수 입력 항목입니다"
            }
        }
        return null
    }

    private fun createMessage(e: MethodArgumentNotValidException): String {
        if (e.fieldError != null && e.fieldError!!.defaultMessage != null) {
            return e.fieldError!!.defaultMessage ?: "알수없는 오류가 발생했습니다."
        }

        return e.fieldErrors.stream()
            .map { obj: FieldError -> obj.field }
            .collect(Collectors.joining(", ")) + " 값들이 정확하지 않습니다."
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<HttpApiResponse<*>> {
        logger.error { "Exception occurred. message=${e.message}" }
        logger.error { e.stackTraceToString() }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                HttpApiResponse.fromExceptionMessage(
                    code = CodeEnum.FRS_004,
                    message = CodeEnum.FRS_004.description
                )
            )
    }
}