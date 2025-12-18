package com.futiland.vote.domain.account.entity

/**
 * Account 도메인에서 Poll 상태 조회용 읽기 전용 Enum
 * poll 테이블의 status 컬럼과 매핑
 */
enum class PollStatusForAccount {
    DRAFT,
    IN_PROGRESS,
    EXPIRED,
    CANCELLED,
    DELETED
}
