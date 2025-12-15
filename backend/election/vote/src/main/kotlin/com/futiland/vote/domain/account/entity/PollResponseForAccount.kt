package com.futiland.vote.domain.account.entity

import jakarta.persistence.*
import org.hibernate.annotations.Immutable
import java.time.LocalDateTime

/**
 * Account 도메인에서 PollResponse 통계 조회용 읽기 전용 엔티티
 * poll_response 테이블을 매핑하지만 Account 도메인 내에서만 사용
 */
@Entity
@Immutable
@Table(name = "poll_response")
class PollResponseForAccount(
    @Id
    val id: Long,
    val pollId: Long,
    val accountId: Long,
    val deletedAt: LocalDateTime?
)
