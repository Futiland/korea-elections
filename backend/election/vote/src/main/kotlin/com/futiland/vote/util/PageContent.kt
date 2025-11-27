package com.futiland.vote.util

import kotlin.math.ceil

/**
 * 페이지네이션 응답 컨테이너
 *
 * @property content 실제 데이터 목록
 * @property totalCount 전체 항목 개수
 * @property totalPages 전체 페이지 수
 *
 * Note: size와 page는 클라이언트가 요청 시 제공했으므로 응답에 포함하지 않습니다.
 */
data class PageContent<T>(
    val content: List<T>,
    val totalCount: Long,
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
