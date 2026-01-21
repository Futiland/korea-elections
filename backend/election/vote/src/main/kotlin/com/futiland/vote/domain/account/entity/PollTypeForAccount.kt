package com.futiland.vote.domain.account.entity

/**
 * Account 도메인에서 Poll 타입 조회용 읽기 전용 Enum
 * poll 테이블의 poll_type 컬럼과 매핑
 */
enum class PollTypeForAccount {
    SYSTEM,
    PUBLIC
}
