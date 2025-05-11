package com.futiland.vote.domain.vote.entity

enum class ElectionStatus(val description: String) {
    ACTIVE("활성"),
    INACTIVE("비활성"),
    EXPIRED("만료"),
    DELETED("삭제됨"),
    ;

}