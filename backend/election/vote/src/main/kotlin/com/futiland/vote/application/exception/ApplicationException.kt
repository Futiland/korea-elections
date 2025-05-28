package com.futiland.vote.application.exception

import com.futiland.vote.application.common.httpresponse.CodeEnum


class ApplicationException : RuntimeException {
    val code: CodeEnum
    val data: Map<String, Any>?

    constructor(code: CodeEnum, message: String?, data: Map<String, Any>?) : super(message) {
        this.code = code
        this.data = data
    }

    constructor(code: CodeEnum, message: String?) : super(message) {
        this.code = code
        this.data = null
    }
}