package com.futiland.vote.application.scheduler

import com.futiland.vote.domain.poll.repository.PollRepository
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

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
