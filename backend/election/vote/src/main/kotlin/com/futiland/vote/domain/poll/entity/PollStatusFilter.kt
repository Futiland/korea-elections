package com.futiland.vote.domain.poll.entity

enum class PollStatusFilter(val description: String, val statuses: List<PollStatus>) {
    ALL("전체", listOf(PollStatus.IN_PROGRESS, PollStatus.EXPIRED)),
    IN_PROGRESS("진행중", listOf(PollStatus.IN_PROGRESS)),
    EXPIRED("기간만료", listOf(PollStatus.EXPIRED))
}
