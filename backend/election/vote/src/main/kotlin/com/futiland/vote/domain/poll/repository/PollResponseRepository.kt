package com.futiland.vote.domain.poll.repository

import com.futiland.vote.domain.poll.entity.PollResponse

interface PollResponseRepository {
    fun save(pollResponse: PollResponse): PollResponse
    fun findById(id: Long): PollResponse?
    fun findByPollIdAndAccountId(pollId: Long, accountId: Long): PollResponse?
    fun findAllByPollId(pollId: Long): List<PollResponse>
    fun countByPollId(pollId: Long): Long
}
