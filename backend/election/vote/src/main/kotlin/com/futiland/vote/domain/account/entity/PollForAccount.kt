package com.futiland.vote.domain.account.entity

import jakarta.persistence.*
import org.hibernate.annotations.Immutable

/**
 * Account 도메인에서 Poll 통계 조회용 읽기 전용 엔티티
 * poll 테이블을 매핑하지만 Account 도메인 내에서만 사용
 */
@Entity
@Immutable
@Table(name = "poll")
class PollForAccount(
    @Id
    val id: Long,

    val creatorAccountId: Long,

    @Enumerated(EnumType.STRING)
    val pollType: PollTypeForAccount,

    @Enumerated(EnumType.STRING)
    val status: PollStatusForAccount
)
