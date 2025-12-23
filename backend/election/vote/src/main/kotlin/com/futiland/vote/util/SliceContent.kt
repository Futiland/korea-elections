package com.futiland.vote.util

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "커서 기반 페이지네이션 응답")
data class SliceContent<T>(
    @Schema(description = "조회 결과 목록")
    val content: List<T>,
    @Schema(description = "다음 페이지 커서 (null이면 마지막 페이지)", example = "eyJpZCI6MTIzfQ==", nullable = true)
    val nextCursor: String?
)