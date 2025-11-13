package com.futiland.vote.domain.poll.repository

import com.futiland.vote.domain.poll.entity.PollResponse

interface PollResponseRepository {
    fun save(pollResponse: PollResponse): PollResponse
    fun saveAll(pollResponses: List<PollResponse>): List<PollResponse>
    fun findById(id: Long): PollResponse?
    fun findByPollIdAndAccountId(pollId: Long, accountId: Long): PollResponse?
    fun findAllByPollIdAndAccountId(pollId: Long, accountId: Long): List<PollResponse>
    fun findAllByPollId(pollId: Long): List<PollResponse>
    fun countByPollId(pollId: Long): Long
    fun countByOptionId(optionId: Long): Long
}
