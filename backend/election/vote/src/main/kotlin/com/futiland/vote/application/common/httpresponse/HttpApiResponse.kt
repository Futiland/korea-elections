package com.futiland.vote.application.common.httpresponse

data class HttpApiResponse<T>(
    val code: CodeEnum = CodeEnum.RS_000,
    val message: String? = null,
    val data: T? = null

) {

    companion object {
        fun<T>ok(): HttpApiResponse<T> {
            return HttpApiResponse(
                code = CodeEnum.RS_000,
                message = CodeEnum.RS_000.description
            )
        }

        fun <T> of(data: T): HttpApiResponse<T> {
            return HttpApiResponse(
                code = CodeEnum.RS_000,
                message = CodeEnum.RS_000.description,
                data = data
            )
        }

        fun fromExceptionMessage(message: String, code: CodeEnum): HttpApiResponse<*> {
            return HttpApiResponse(
                code = code,
                message = message,
                data = null
            )
        }

        fun fromExceptionMessage(message: String, code: CodeEnum, data: Map<String, Any>): HttpApiResponse<*> {
            return HttpApiResponse(
                code = code,
                message = message,
                data = data
            )
        }

    }
}