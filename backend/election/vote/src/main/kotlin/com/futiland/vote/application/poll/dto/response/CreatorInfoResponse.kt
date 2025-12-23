package com.futiland.vote.application.poll.dto.response

import com.futiland.vote.application.aop.masking.Masked
import com.futiland.vote.application.aop.masking.MaskingType
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "작성자 정보")
data class CreatorInfoResponse(
    @Schema(description = "작성자 계정 ID", example = "1")
    val accountId: Long,
    @Schema(description = "작성자 이름", example = "홍**")
    @Masked(type = MaskingType.NAME)
    val name: String,
)
