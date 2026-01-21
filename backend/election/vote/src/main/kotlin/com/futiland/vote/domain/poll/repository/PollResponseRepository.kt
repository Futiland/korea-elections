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

    /**
     * 해당 Poll에 참여한 고유 사용자 수 (multichoice에서 중복 카운트 방지)
     */
    fun countDistinctParticipantsByPollId(pollId: Long): Long
    fun countByOptionId(optionId: Long): Long

    /**
     * No Offset 방식으로 내가 참여한 투표 조회 (커버링 인덱스 사용)
     * idx_account_id (accountId, id, deletedAt) 인덱스 활용
     *
     * @param accountId 계정 ID
     * @param lastId 마지막으로 조회한 PollResponse ID (최신순이므로 id > lastId)
     * @param size 조회할 개수
     */
    fun findParticipatedPollsByAccountId(accountId: Long, lastId: Long?, size: Int): List<PollResponse>

    /**
     * 내가 참여한 투표 전체 개수 조회
     */
    fun countByAccountId(accountId: Long): Long

    /**
     * 주어진 pollIds 중 해당 사용자가 투표한 pollId 목록 조회
     */
    fun findVotedPollIds(accountId: Long, pollIds: List<Long>): Set<Long>
}
