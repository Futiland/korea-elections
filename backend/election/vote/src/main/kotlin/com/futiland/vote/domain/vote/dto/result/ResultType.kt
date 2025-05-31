package com.futiland.vote.domain.vote.dto.result

enum class ResultType(val description: String, val columnName: String) {
    ALL("전체", "all"),
    GENDER("성별", "gender"),
    AGE("나이", "age"),
}