package com.futiland.vote.domain.poll.repository

import com.futiland.vote.domain.poll.entity.Poll
import com.futiland.vote.util.SliceContent

interface PollRepository {
    fun save(poll: Poll): Poll
    fun getById(id: Long): Poll
    fun findById(id: Long): Poll?
    fun findAllPublicDisplayable(size: Int, nextCursor: String?): SliceContent<Poll>
    fun findAllByCreatorAccountId(creatorAccountId: Long): List<Poll>
}
