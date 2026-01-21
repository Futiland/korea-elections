package com.futiland.vote.util

import io.swagger.v3.oas.annotations.media.Schema
import kotlin.math.ceil

@Schema(description = "페이지 기반 페이지네이션 응답")
data class PageContent<T>(
    @Schema(description = "조회 결과 목록")
    val content: List<T>,
    @Schema(description = "전체 항목 개수", example = "100")
    val totalCount: Long,
    @Schema(description = "전체 페이지 수", example = "10")
    val totalPages: Int
) {
    companion object {
        /**
         * PageContent 인스턴스 생성
         *
         * @param content 현재 페이지의 데이터 목록
         * @param totalElements 전체 항목 개수
         * @param size 페이지 크기 (totalPages 계산용, 응답에는 포함 안 됨)
         */
        fun <T> of(content: List<T>, totalElements: Long, size: Int): PageContent<T> {
            val totalPages = if (totalElements > 0) {
                ceil(totalElements.toDouble() / size).toInt()
            } else {
                0
            }

            return PageContent(
                content = content,
                totalCount = totalElements,
                totalPages = totalPages
            )
        }
    }
}
