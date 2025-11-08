package com.futiland.vote.domain.poll.repository

import com.futiland.vote.domain.poll.entity.PollOption

interface PollOptionRepository {
    fun save(pollOption: PollOption): PollOption
    fun saveAll(pollOptions: List<PollOption>): List<PollOption>
    fun findById(id: Long): PollOption?
    fun findAllByPollId(pollId: Long): List<PollOption>
    fun deleteAllByPollId(pollId: Long)
}
