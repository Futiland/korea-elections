package com.futiland.vote.domain.poll.entity

enum class PollSortType(val description: String) {
    LATEST("최신순"),
    POPULAR("인기순"),
    ENDING_SOON("마감 임박순")
}
