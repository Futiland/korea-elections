package com.futiland.vote.domain.poll.entity

import com.futiland.vote.application.common.EncryptConverter
import jakarta.persistence.*
import org.hibernate.annotations.Immutable

/**
 * Poll 도메인에서 Account 정보 조회용 읽기 전용 엔티티
 * account 테이블을 매핑하지만 Poll 도메인 내에서만 사용
 */
@Entity
@Immutable
@Table(name = "account")
class AccountForPoll(
    @Id
    val id: Long,

    @Convert(converter = EncryptConverter::class)
    val name: String,
)
