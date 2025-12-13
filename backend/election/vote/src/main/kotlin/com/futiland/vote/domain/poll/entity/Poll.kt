package com.futiland.vote.domain.poll.entity

import com.futiland.vote.application.poll.dto.request.PollResponseSubmitRequest
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Poll(
    var title: String,
    var description: String,
    @Enumerated(EnumType.STRING)
    val responseType: ResponseType,
    @Enumerated(EnumType.STRING)
    val pollType: PollType,
    initialStatus: PollStatus,
    val isRevotable: Boolean,
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
            responseType: ResponseType,
            pollType: PollType,
            isRevotable: Boolean,
            creatorAccountId: Long,
        ): Poll {
            return Poll(
                title = title,
                description = description,
                responseType = responseType,
                pollType = pollType,
                initialStatus = PollStatus.DRAFT,
                isRevotable = isRevotable,
                creatorAccountId = creatorAccountId,
                startAt = null,
                endAt = null
            )
        }

        fun createActive(
            title: String,
            description: String,
            responseType: ResponseType,
            pollType: PollType,
            isRevotable: Boolean,
            creatorAccountId: Long,
            endAt: LocalDateTime,
        ): Poll {
            return Poll(
                title = title,
                description = description,
                responseType = responseType,
                pollType = pollType,
                initialStatus = PollStatus.IN_PROGRESS,
                isRevotable = isRevotable,
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

    fun validateResponse(request: PollResponseSubmitRequest) {
        // 1. Poll의 responseType과 요청의 responseType 일치 검증
        require(request.responseType == this.responseType) {
            "여론조사 타입(${this.responseType})과 응답 타입(${request.responseType})이 일치하지 않습니다"
        }

        // 2. 각 타입별 세부 검증 (기본 null/empty 검증은 Bean Validation에서 처리)
        when (request) {
            is PollResponseSubmitRequest.SingleChoice -> {
                // optionId는 이미 @NotNull로 검증됨
            }
            is PollResponseSubmitRequest.MultipleChoice -> {
                // optionIds는 이미 @NotEmpty로 검증됨
            }
            is PollResponseSubmitRequest.Score -> {
                require(request.scoreValue in 0..10) { "점수는 0~10 사이여야 합니다" }
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

    fun expire() {
        require(status == PollStatus.IN_PROGRESS) {
            "진행중인 여론조사만 만료할 수 있습니다"
        }
        status = PollStatus.EXPIRED
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
