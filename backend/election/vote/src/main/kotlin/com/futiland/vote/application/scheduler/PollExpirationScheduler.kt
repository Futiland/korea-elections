package com.futiland.vote.application.scheduler

import com.futiland.vote.domain.poll.repository.PollRepository
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

// TODO : 스케쥴러 서버만 따로 추출해서 사용할 수 있도록 할것. 현재는 작은 서버이기 때문에 이렇게 동작하지만 스케일 아웃 하는 순간 성능이 좋지 않게됨.

@Component
class PollExpirationScheduler(
    private val pollRepository: PollRepository
) {
    private val logger = KotlinLogging.logger {}

    @Scheduled(fixedDelayString = "\${scheduler.poll.expiration-check-interval}")
    @Transactional
    fun expirePolls() {
        val now = LocalDateTime.now()
        val expiredCount = pollRepository.expireOverduePolls(now)

        if (expiredCount > 0) {
            logger.info { "만료 처리 완료: ${expiredCount}건" }
        }
    }
}
