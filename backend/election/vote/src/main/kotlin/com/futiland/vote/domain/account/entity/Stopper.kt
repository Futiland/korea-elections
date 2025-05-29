package com.futiland.vote.domain.account.entity

import jakarta.persistence.*

/**
 * 회원가입을 막는 스토퍼 엔티티, 추후 회원가입 말고 다른것들도 막을 수 있거나 제어를 할 수 있을 것으로 보임.
 */
@Entity
class Stopper(
    serviceTarget: ServiceTarget,
    stopperStatus: StopperStatus,
    val message: String = ""
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    var serviceTarget: ServiceTarget = serviceTarget
        private set

    @Enumerated(EnumType.STRING)
    var status: StopperStatus = stopperStatus
        private set
}