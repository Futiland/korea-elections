package com.futiland.vote.application.common.httpresponse


enum class CodeEnum(val description: String) {
    RS_000("성공"),
    FRS_001("데이터 없음"),
    FRS_002("권한 없음"),
    FRS_003("Invalid Request"),
    FRS_004("Unknown Error"),
}