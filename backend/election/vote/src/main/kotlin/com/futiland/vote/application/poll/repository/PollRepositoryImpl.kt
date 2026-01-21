package com.futiland.vote.application.poll.repository

import com.futiland.vote.domain.poll.entity.Poll
import com.futiland.vote.domain.poll.entity.PollSortType
import com.futiland.vote.domain.poll.entity.PollStatus
import com.futiland.vote.domain.poll.entity.PollStatusFilter
import com.futiland.vote.domain.poll.entity.PollType
import com.futiland.vote.domain.poll.repository.PollRepository
import com.futiland.vote.domain.poll.repository.PollResponseRepository
import com.futiland.vote.util.PageContent
import com.futiland.vote.util.SliceContent
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class PollRepositoryImpl(
    private val repository: JpaPollRepository,
    private val pollResponseRepository: PollResponseRepository
) : PollRepository {
    override fun save(poll: Poll): Poll {
        return repository.save(poll)
    }

    override fun getById(id: Long): Poll {
        return repository.findById(id).orElseThrow {
            IllegalArgumentException("여론조사를 찾을 수 없습니다.")
        }
    }

    override fun findById(id: Long): Poll? {
        return repository.findById(id).orElse(null)
    }

    override fun findAllPublicDisplayable(
        size: Int,
        nextCursor: String?,
        sortType: PollSortType,
        statusFilter: PollStatusFilter
    ): SliceContent<Poll> {
        return findAllDisplayableByType(
            pollType = PollType.PUBLIC,
            size = size,
            nextCursor = nextCursor,
            sortType = sortType,
            statusFilter = statusFilter
        )
    }

    override fun findAllSystemDisplayable(
        size: Int,
        nextCursor: String?,
        sortType: PollSortType,
        statusFilter: PollStatusFilter
    ): SliceContent<Poll> {
        return findAllDisplayableByType(
            pollType = PollType.SYSTEM,
            size = size,
            nextCursor = nextCursor,
            sortType = sortType,
            statusFilter = statusFilter
        )
    }

    /**
     * 공통 조회 로직: 타입별 여론조사 목록 조회 (정렬/필터 지원)
     */
    private fun findAllDisplayableByType(
        pollType: PollType,
        size: Int,
        nextCursor: String?,
        sortType: PollSortType,
        statusFilter: PollStatusFilter
    ): SliceContent<Poll> {
        val pageable = PageRequest.ofSize(size)
        val statuses = statusFilter.statuses

        val content = when (sortType) {
            PollSortType.LATEST -> {
                if (nextCursor == null) {
                    repository.getPollsFromLatestByType(
                        status = statuses,
                        pollType = pollType,
                        pageable = pageable
                    )
                } else {
                    repository.getPollsFromLastIdByType(
                        pollId = nextCursor.toLong(),
                        status = statuses,
                        pollType = pollType,
                        pageable = pageable
                    )
                }
            }
            PollSortType.POPULAR -> {
                // TODO: 추후 Poll 엔티티에 responseCount 필드 추가하여 최적화
                //       현재는 서브쿼리로 매번 COUNT 계산
                val parsedCursor = nextCursor?.let { parsePopularCursor(it) }
                if (parsedCursor == null) {
                    // 첫 페이지 또는 잘못된 커서 형식인 경우
                    repository.getPollsByPopularityByType(
                        statuses = statuses,
                        pollType = pollType,
                        pageable = pageable
                    )
                } else {
                    val (cursorCount, cursorId) = parsedCursor
                    repository.getPollsByPopularityFromCursorByType(
                        cursorCount = cursorCount,
                        cursorId = cursorId,
                        statuses = statuses,
                        pollType = pollType,
                        pageable = pageable
                    )
                }
            }
            PollSortType.ENDING_SOON -> {
                val now = LocalDateTime.now()
                val parsedCursor = nextCursor?.let { parseEndingSoonCursor(it) }
                if (parsedCursor == null) {
                    repository.getPollsByEndingSoonByType(
                        now = now,
                        statuses = statuses,
                        pollType = pollType,
                        pageable = pageable
                    )
                } else {
                    val (cursorEndAt, cursorId) = parsedCursor
                    repository.getPollsByEndingSoonFromCursorByType(
                        now = now,
                        cursorEndAt = cursorEndAt,
                        cursorId = cursorId,
                        statuses = statuses,
                        pollType = pollType,
                        pageable = pageable
                    )
                }
            }
        }

        val cursor = buildNextCursor(content, size, sortType)
        return SliceContent(content, cursor)
    }

    /**
     * 인기순 커서 파싱: "{responseCount}_{pollId}" -> Pair(responseCount, pollId)
     * 잘못된 형식의 커서가 전달되면 null 반환 (첫 페이지로 처리)
     */
    private fun parsePopularCursor(cursor: String): Pair<Long, Long>? {
        val parts = cursor.split("_")
        if (parts.size != 2) return null
        return try {
            Pair(parts[0].toLong(), parts[1].toLong())
        } catch (e: NumberFormatException) {
            null
        }
    }

    /**
     * 마감 임박순 커서 파싱: "{endAt}_{pollId}" -> Pair(endAt, pollId)
     * 잘못된 형식의 커서가 전달되면 null 반환 (첫 페이지로 처리)
     */
    private fun parseEndingSoonCursor(cursor: String): Pair<LocalDateTime, Long>? {
        val parts = cursor.split("_")
        if (parts.size != 2) return null
        return try {
            Pair(LocalDateTime.parse(parts[0]), parts[1].toLong())
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 다음 페이지 커서 생성
     */
    private fun buildNextCursor(content: List<Poll>, size: Int, sortType: PollSortType): String? {
        if (content.isEmpty() || content.size < size) return null

        val lastPoll = content.last()
        return when (sortType) {
            PollSortType.LATEST -> lastPoll.id.toString()
            PollSortType.POPULAR -> {
                val lastResponseCount = pollResponseRepository.countDistinctParticipantsByPollId(lastPoll.id)
                "${lastResponseCount}_${lastPoll.id}"
            }
            PollSortType.ENDING_SOON -> {
                val endAt = lastPoll.endAt ?: return null
                "${endAt}_${lastPoll.id}"
            }
        }
    }

    override fun findAllByIdIn(ids: List<Long>): List<Poll> {
        if (ids.isEmpty()) return emptyList()
        return repository.findAllByIdInExcludeStatus(ids, PollStatus.DELETED)
    }

    override fun findMyPollsWithPage(creatorAccountId: Long, page: Int, size: Int): PageContent<Poll> {
        val pageable = PageRequest.of(page - 1, size)
        val content = repository.findByCreatorAccountIdExcludeStatus(creatorAccountId, PollStatus.DELETED, pageable)
        val totalCount = repository.countByCreatorAccountIdExcludeStatus(creatorAccountId, PollStatus.DELETED)
        return PageContent.of(content, totalCount, size)
    }

    override fun expireOverduePolls(now: LocalDateTime): Int {
        return repository.expireOverduePolls(now, PollStatus.IN_PROGRESS, PollStatus.EXPIRED)
    }

    override fun searchPublicPolls(
        keyword: String,
        size: Int,
        nextCursor: String?,
        sortType: PollSortType,
        statusFilter: PollStatusFilter
    ): SliceContent<Poll> {
        return searchByKeywordAndType(
            keyword = keyword,
            pollType = PollType.PUBLIC,
            size = size,
            nextCursor = nextCursor,
            sortType = sortType,
            statusFilter = statusFilter
        )
    }

    override fun searchSystemPolls(
        keyword: String,
        size: Int,
        nextCursor: String?,
        sortType: PollSortType,
        statusFilter: PollStatusFilter
    ): SliceContent<Poll> {
        return searchByKeywordAndType(
            keyword = keyword,
            pollType = PollType.SYSTEM,
            size = size,
            nextCursor = nextCursor,
            sortType = sortType,
            statusFilter = statusFilter
        )
    }

    /**
     * 공통 검색 로직: 타입별 여론조사 키워드 검색 (정렬/필터 지원)
     */
    private fun searchByKeywordAndType(
        keyword: String,
        pollType: PollType,
        size: Int,
        nextCursor: String?,
        sortType: PollSortType,
        statusFilter: PollStatusFilter
    ): SliceContent<Poll> {
        val pageable = PageRequest.ofSize(size)
        val statuses = statusFilter.statuses

        val content = when (sortType) {
            PollSortType.LATEST -> {
                if (nextCursor == null) {
                    repository.searchPollsLatestByType(
                        keyword = keyword,
                        statuses = statuses,
                        pollType = pollType,
                        pageable = pageable
                    )
                } else {
                    repository.searchPollsLatestFromCursorByType(
                        keyword = keyword,
                        cursorId = nextCursor.toLong(),
                        statuses = statuses,
                        pollType = pollType,
                        pageable = pageable
                    )
                }
            }
            PollSortType.POPULAR -> {
                val parsedCursor = nextCursor?.let { parsePopularCursor(it) }
                if (parsedCursor == null) {
                    repository.searchPollsPopularByType(
                        keyword = keyword,
                        statuses = statuses,
                        pollType = pollType,
                        pageable = pageable
                    )
                } else {
                    val (cursorCount, cursorId) = parsedCursor
                    repository.searchPollsPopularFromCursorByType(
                        keyword = keyword,
                        cursorCount = cursorCount,
                        cursorId = cursorId,
                        statuses = statuses,
                        pollType = pollType,
                        pageable = pageable
                    )
                }
            }
            PollSortType.ENDING_SOON -> {
                val now = LocalDateTime.now()
                val parsedCursor = nextCursor?.let { parseEndingSoonCursor(it) }
                if (parsedCursor == null) {
                    repository.searchPollsEndingSoonByType(
                        keyword = keyword,
                        now = now,
                        statuses = statuses,
                        pollType = pollType,
                        pageable = pageable
                    )
                } else {
                    val (cursorEndAt, cursorId) = parsedCursor
                    repository.searchPollsEndingSoonFromCursorByType(
                        keyword = keyword,
                        now = now,
                        cursorEndAt = cursorEndAt,
                        cursorId = cursorId,
                        statuses = statuses,
                        pollType = pollType,
                        pageable = pageable
                    )
                }
            }
        }

        val cursor = buildNextCursor(content, size, sortType)
        return SliceContent(content, cursor)
    }
}

interface JpaPollRepository : JpaRepository<Poll, Long> {

    @Query(
        """
        SELECT p FROM Poll p
        WHERE p.id IN :ids
        AND p.status != :excludeStatus
        """
    )
    fun findAllByIdInExcludeStatus(ids: List<Long>, excludeStatus: PollStatus): List<Poll>

    @Query(
        """
        SELECT COUNT(p) FROM Poll p
        WHERE p.creatorAccountId = :creatorAccountId
        AND p.status != :excludeStatus
        """
    )
    fun countByCreatorAccountIdExcludeStatus(creatorAccountId: Long, excludeStatus: PollStatus): Long

    @Query(
        """
        SELECT p FROM Poll p
        WHERE p.creatorAccountId = :creatorAccountId
        AND p.status != :excludeStatus
        ORDER BY p.id DESC
        """
    )
    fun findByCreatorAccountIdExcludeStatus(creatorAccountId: Long, excludeStatus: PollStatus, pageable: PageRequest): List<Poll>

    @Query(
        """
        SELECT p FROM Poll p
        WHERE p.status in :status
        ORDER BY p.id DESC
        """
    )
    fun getPollsFromLatest(status: List<PollStatus>, pageable: PageRequest): List<Poll>

    @Query(
        """
        SELECT p FROM Poll p
        WHERE p.id < :pollId and p.status in :status
        ORDER BY p.id DESC
        """
    )
    fun getPollsFromLastId(
        pollId: Long,
        status: List<PollStatus>,
        pageable: PageRequest
    ): List<Poll>

    @Query(
        """
        SELECT p FROM Poll p
        WHERE p.status in :status AND p.pollType = :pollType
        ORDER BY p.id DESC
        """
    )
    fun getPollsFromLatestByType(status: List<PollStatus>, pollType: PollType, pageable: PageRequest): List<Poll>

    @Query(
        """
        SELECT p FROM Poll p
        WHERE p.id < :pollId AND p.status in :status AND p.pollType = :pollType
        ORDER BY p.id DESC
        """
    )
    fun getPollsFromLastIdByType(
        pollId: Long,
        status: List<PollStatus>,
        pollType: PollType,
        pageable: PageRequest
    ): List<Poll>

    @Modifying
    @Query(
        """
        UPDATE Poll p
        SET p.status = :expiredStatus, p.updatedAt = :now
        WHERE p.status = :inProgressStatus
        AND p.endAt < :now
        """
    )
    fun expireOverduePolls(now: LocalDateTime, inProgressStatus: PollStatus, expiredStatus: PollStatus): Int

    /**
     * 인기순 조회 - 첫 페이지 (참여 수 많은 순)
     */
    @Query(
        """
        SELECT p FROM Poll p
        WHERE p.status IN :statuses AND p.pollType = :pollType
        ORDER BY (
            SELECT COUNT(DISTINCT pr.accountId) FROM PollResponse pr WHERE pr.pollId = p.id AND pr.deletedAt IS NULL
        ) DESC, p.id DESC
        """
    )
    fun getPollsByPopularityByType(
        statuses: List<PollStatus>,
        pollType: PollType,
        pageable: PageRequest
    ): List<Poll>

    /**
     * 인기순 조회 - 커서 기반 다음 페이지
     */
    @Query(
        """
        SELECT p FROM Poll p
        WHERE p.status IN :statuses AND p.pollType = :pollType
        AND (
            (SELECT COUNT(DISTINCT pr.accountId) FROM PollResponse pr WHERE pr.pollId = p.id AND pr.deletedAt IS NULL) < :cursorCount
            OR (
                (SELECT COUNT(DISTINCT pr.accountId) FROM PollResponse pr WHERE pr.pollId = p.id AND pr.deletedAt IS NULL) = :cursorCount
                AND p.id < :cursorId
            )
        )
        ORDER BY (
            SELECT COUNT(DISTINCT pr.accountId) FROM PollResponse pr WHERE pr.pollId = p.id AND pr.deletedAt IS NULL
        ) DESC, p.id DESC
        """
    )
    fun getPollsByPopularityFromCursorByType(
        cursorCount: Long,
        cursorId: Long,
        statuses: List<PollStatus>,
        pollType: PollType,
        pageable: PageRequest
    ): List<Poll>

    // ===== 키워드 검색 쿼리 (타입별) =====

    /**
     * 키워드 검색 - 최신순 첫 페이지 (타입별)
     */
    @Query(
        """
        SELECT p FROM Poll p
        WHERE p.status IN :statuses
        AND p.pollType = :pollType
        AND (p.title LIKE %:keyword% OR p.description LIKE %:keyword%)
        ORDER BY p.id DESC
        """
    )
    fun searchPollsLatestByType(
        keyword: String,
        statuses: List<PollStatus>,
        pollType: PollType,
        pageable: PageRequest
    ): List<Poll>

    /**
     * 키워드 검색 - 최신순 커서 기반 다음 페이지 (타입별)
     */
    @Query(
        """
        SELECT p FROM Poll p
        WHERE p.id < :cursorId
        AND p.status IN :statuses
        AND p.pollType = :pollType
        AND (p.title LIKE %:keyword% OR p.description LIKE %:keyword%)
        ORDER BY p.id DESC
        """
    )
    fun searchPollsLatestFromCursorByType(
        keyword: String,
        cursorId: Long,
        statuses: List<PollStatus>,
        pollType: PollType,
        pageable: PageRequest
    ): List<Poll>

    /**
     * 키워드 검색 - 인기순 첫 페이지 (타입별)
     */
    @Query(
        """
        SELECT p FROM Poll p
        WHERE p.status IN :statuses
        AND p.pollType = :pollType
        AND (p.title LIKE %:keyword% OR p.description LIKE %:keyword%)
        ORDER BY (
            SELECT COUNT(DISTINCT pr.accountId) FROM PollResponse pr WHERE pr.pollId = p.id AND pr.deletedAt IS NULL
        ) DESC, p.id DESC
        """
    )
    fun searchPollsPopularByType(
        keyword: String,
        statuses: List<PollStatus>,
        pollType: PollType,
        pageable: PageRequest
    ): List<Poll>

    /**
     * 키워드 검색 - 인기순 커서 기반 다음 페이지 (타입별)
     */
    @Query(
        """
        SELECT p FROM Poll p
        WHERE p.status IN :statuses
        AND p.pollType = :pollType
        AND (p.title LIKE %:keyword% OR p.description LIKE %:keyword%)
        AND (
            (SELECT COUNT(DISTINCT pr.accountId) FROM PollResponse pr WHERE pr.pollId = p.id AND pr.deletedAt IS NULL) < :cursorCount
            OR (
                (SELECT COUNT(DISTINCT pr.accountId) FROM PollResponse pr WHERE pr.pollId = p.id AND pr.deletedAt IS NULL) = :cursorCount
                AND p.id < :cursorId
            )
        )
        ORDER BY (
            SELECT COUNT(DISTINCT pr.accountId) FROM PollResponse pr WHERE pr.pollId = p.id AND pr.deletedAt IS NULL
        ) DESC, p.id DESC
        """
    )
    fun searchPollsPopularFromCursorByType(
        keyword: String,
        cursorCount: Long,
        cursorId: Long,
        statuses: List<PollStatus>,
        pollType: PollType,
        pageable: PageRequest
    ): List<Poll>

    // ===== 마감 임박순 조회 쿼리 =====

    /**
     * 마감 임박순 조회 - 첫 페이지 (endAt이 현재보다 크고, 가장 가까운 순)
     */
    @Query(
        """
        SELECT p FROM Poll p
        WHERE p.status IN :statuses AND p.pollType = :pollType
        AND p.endAt IS NOT NULL AND p.endAt > :now
        ORDER BY p.endAt ASC, p.id ASC
        """
    )
    fun getPollsByEndingSoonByType(
        now: LocalDateTime,
        statuses: List<PollStatus>,
        pollType: PollType,
        pageable: PageRequest
    ): List<Poll>

    /**
     * 마감 임박순 조회 - 커서 기반 다음 페이지
     */
    @Query(
        """
        SELECT p FROM Poll p
        WHERE p.status IN :statuses AND p.pollType = :pollType
        AND p.endAt IS NOT NULL AND p.endAt > :now
        AND (
            p.endAt > :cursorEndAt
            OR (p.endAt = :cursorEndAt AND p.id > :cursorId)
        )
        ORDER BY p.endAt ASC, p.id ASC
        """
    )
    fun getPollsByEndingSoonFromCursorByType(
        now: LocalDateTime,
        cursorEndAt: LocalDateTime,
        cursorId: Long,
        statuses: List<PollStatus>,
        pollType: PollType,
        pageable: PageRequest
    ): List<Poll>

    // ===== 키워드 검색 - 마감 임박순 쿼리 =====

    /**
     * 키워드 검색 - 마감 임박순 첫 페이지 (타입별)
     */
    @Query(
        """
        SELECT p FROM Poll p
        WHERE p.status IN :statuses
        AND p.pollType = :pollType
        AND (p.title LIKE %:keyword% OR p.description LIKE %:keyword%)
        AND p.endAt IS NOT NULL AND p.endAt > :now
        ORDER BY p.endAt ASC, p.id ASC
        """
    )
    fun searchPollsEndingSoonByType(
        keyword: String,
        now: LocalDateTime,
        statuses: List<PollStatus>,
        pollType: PollType,
        pageable: PageRequest
    ): List<Poll>

    /**
     * 키워드 검색 - 마감 임박순 커서 기반 다음 페이지 (타입별)
     */
    @Query(
        """
        SELECT p FROM Poll p
        WHERE p.status IN :statuses
        AND p.pollType = :pollType
        AND (p.title LIKE %:keyword% OR p.description LIKE %:keyword%)
        AND p.endAt IS NOT NULL AND p.endAt > :now
        AND (
            p.endAt > :cursorEndAt
            OR (p.endAt = :cursorEndAt AND p.id > :cursorId)
        )
        ORDER BY p.endAt ASC, p.id ASC
        """
    )
    fun searchPollsEndingSoonFromCursorByType(
        keyword: String,
        now: LocalDateTime,
        cursorEndAt: LocalDateTime,
        cursorId: Long,
        statuses: List<PollStatus>,
        pollType: PollType,
        pageable: PageRequest
    ): List<Poll>

}
