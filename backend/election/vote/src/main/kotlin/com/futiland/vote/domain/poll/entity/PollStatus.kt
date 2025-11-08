package com.futiland.vote.domain.poll.entity

enum class PollStatus(val description: String) {
    DRAFT("작성중"),
    IN_PROGRESS("진행중"),
    EXPIRED("기간만료"),
    CANCELLED("취소됨"),
    DELETED("삭제됨")
}
