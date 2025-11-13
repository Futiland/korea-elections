package com.futiland.vote.domain.poll.entity

import com.futiland.vote.application.common.httpresponse.CodeEnum
import com.futiland.vote.application.exception.ApplicationException
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Poll(
    var title: String,
    var description: String,
    @Enumerated(EnumType.STRING)
    val questionType: QuestionType,
    @Enumerated(EnumType.STRING)
    val pollType: PollType,
    initialStatus: PollStatus,
    val allowMultipleResponses: Boolean,
    val creatorAccountId: Long,
    var startAt: LocalDateTime? = null,
    var endAt: LocalDateTime? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L

    @Enumerated(EnumType.STRING)
    var status: PollStatus = determineStatus(initialStatus)
        private set

    var updatedAt: LocalDateTime? = null
        private set

    var deletedAt: LocalDateTime? = null
        private set

    companion object {
        fun createDraft(
            title: String,
            description: String,
            questionType: QuestionType,
            pollType: PollType,
            allowMultipleResponses: Boolean,
            creatorAccountId: Long,
        ): Poll {
            return Poll(
                title = title,
                description = description,
                questionType = questionType,
                pollType = pollType,
                initialStatus = PollStatus.DRAFT,
                allowMultipleResponses = allowMultipleResponses,
                creatorAccountId = creatorAccountId,
                startAt = null,
                endAt = null
            )
        }

        fun createActive(
            title: String,
            description: String,
            questionType: QuestionType,
            pollType: PollType,
            allowMultipleResponses: Boolean,
            creatorAccountId: Long,
            endAt: LocalDateTime,
        ): Poll {
            return Poll(
                title = title,
                description = description,
                questionType = questionType,
                pollType = pollType,
                initialStatus = PollStatus.IN_PROGRESS,
                allowMultipleResponses = allowMultipleResponses,
                creatorAccountId = creatorAccountId,
                startAt = LocalDateTime.now(), // 현재 시간으로 자동 설정
                endAt = endAt
            )
        }
    }

    private fun determineStatus(initialStatus: PollStatus): PollStatus {
        if (initialStatus == PollStatus.DELETED) return PollStatus.DELETED
        if (initialStatus == PollStatus.CANCELLED) return PollStatus.CANCELLED
        if (initialStatus == PollStatus.DRAFT) return PollStatus.DRAFT

        val now = LocalDateTime.now()
        return when {
            startAt == null || endAt == null -> PollStatus.DRAFT
            now.isBefore(startAt) -> PollStatus.DRAFT
            now.isAfter(endAt) -> PollStatus.EXPIRED
            else -> PollStatus.IN_PROGRESS
        }
    }

    fun validateResponse(optionIds: List<Long>?, scoreValue: Int?) {
        when (questionType) {
            QuestionType.SINGLE_CHOICE -> {
                require(optionIds != null && optionIds.size == 1) { "단일 선택은 1개만 선택 가능합니다" }
                require(scoreValue == null) { "선택지 타입에는 점수 입력이 불가능합니다" }
            }
            QuestionType.MULTIPLE_CHOICE -> {
                require(optionIds != null && optionIds.isNotEmpty()) { "선택지를 입력해야 합니다" }
                // 다중 선택 시 제한 없음 (minSelections, maxSelections 검증 제거)
                require(scoreValue == null) { "선택지 타입에는 점수 입력이 불가능합니다" }
            }
            QuestionType.SCORE -> {
                require(scoreValue != null) { "점수를 입력해야 합니다" }
                require(scoreValue in 0..10) { "점수는 0~10 사이여야 합니다" }
                require(optionIds.isNullOrEmpty()) { "점수제는 선택지를 선택할 수 없습니다" }
            }
        }
    }

    fun delete() {
        status = PollStatus.DELETED
        deletedAt = LocalDateTime.now()
        updatedAt = LocalDateTime.now()
    }

    fun cancel() {
        require(status == PollStatus.DRAFT || status == PollStatus.IN_PROGRESS) {
            "취소는 작성중이거나 진행중인 여론조사만 가능합니다"
        }
        status = PollStatus.CANCELLED
        updatedAt = LocalDateTime.now()
    }

    fun update(
        title: String?,
        description: String?,
        startAt: LocalDateTime?,
        endAt: LocalDateTime?,
    ) {
        require(status == PollStatus.DRAFT) { "작성중인 여론조사만 수정 가능합니다" }

        title?.let { this.title = it }
        description?.let { this.description = it }
        startAt?.let { this.startAt = it }
        endAt?.let { this.endAt = it }

        updatedAt = LocalDateTime.now()
    }
}
