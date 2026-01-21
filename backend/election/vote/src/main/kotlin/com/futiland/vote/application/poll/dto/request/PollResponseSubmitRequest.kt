package com.futiland.vote.application.poll.dto.request

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.futiland.vote.domain.poll.entity.ResponseType
import jakarta.validation.constraints.NotEmpty

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "responseType"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = PollResponseSubmitRequest.SingleChoice::class, name = "SINGLE_CHOICE"),
    JsonSubTypes.Type(value = PollResponseSubmitRequest.MultipleChoice::class, name = "MULTIPLE_CHOICE"),
    JsonSubTypes.Type(value = PollResponseSubmitRequest.Score::class, name = "SCORE")
)
sealed interface PollResponseSubmitRequest {
    val responseType: ResponseType

    data class SingleChoice(
        @field:NotEmpty(message = "옵션 ID는 필수입니다")
        val optionId: Long
    ) : PollResponseSubmitRequest {
        override val responseType = ResponseType.SINGLE_CHOICE
    }

    data class MultipleChoice(
        @field:NotEmpty(message = "최소 1개 이상의 옵션을 선택해야 합니다")
        val optionIds: List<Long>
    ) : PollResponseSubmitRequest {
        override val responseType = ResponseType.MULTIPLE_CHOICE
    }

    data class Score(
        @field:NotEmpty(message = "점수는 필수입니다")
        val scoreValue: Int
    ) : PollResponseSubmitRequest {
        override val responseType = ResponseType.SCORE
    }
}
