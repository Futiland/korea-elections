package com.futiland.vote.domain.poll.entity

enum class ResponseType(val description: String) {
    SINGLE_CHOICE("단일 선택"),
    MULTIPLE_CHOICE("다중 선택"),
    SCORE("점수제")
}
