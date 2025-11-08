package com.futiland.vote.domain.account.service

import com.futiland.vote.application.account.dto.response.ProfileResponse
import com.futiland.vote.application.account.dto.response.StopperResponse
import com.futiland.vote.application.common.httpresponse.CodeEnum
import com.futiland.vote.application.exception.ApplicationException
import com.futiland.vote.domain.account.entity.AccountStatus
import com.futiland.vote.domain.account.entity.ServiceTarget
import com.futiland.vote.domain.account.entity.Stopper
import com.futiland.vote.domain.account.repository.AccountRepository
import com.futiland.vote.domain.account.repository.StopperRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class AccountQueryService(
    private val accountRepository: AccountRepository,
    private val stopperRepository: StopperRepository,
    @Value("\${account.re-signup-waiting-days}")
    private val reSignupWaitingDays: Int
) : AccountQueryUseCase {
    override fun getProfileById(id: Long): ProfileResponse {
        val account = accountRepository.getById(id = id)
        return ProfileResponse(
            id = account.id,
            phoneNumber = account.phoneNumber,
            name = account.name,
            createdAt = account.createdAt.toString()
        )
    }

    override fun getSignupStopper(): StopperResponse {
        val stopper: Stopper = stopperRepository.getStopper(serviceTarget = ServiceTarget.SIGNUP)
        return StopperResponse(
            status = stopper.status,
            message = stopper.status.description,
        )
    }

    /**
     * 회원가입 가능 여부 검증 (중복 가입 체크 + 재가입 대기 기간 검증을 단일 조회로 처리)
     *
     * 성능 최적화:
     * - JPA 1차 캐시는 PK 기반이므로 CI 조회는 캐시되지 않음
     * - 두 검증을 분리하면 동일한 CI로 2번 DB 조회 발생
     * - 통합하여 1번의 DB 조회로 모든 검증 완료
     */
    override fun validateSignUpEligibility(ci: String) {
        val account = accountRepository.findByCi(ci) ?: return

        // 1. 활성화된 계정이 있는 경우 -> 중복 가입 불가
        if (account.status == AccountStatus.ACTIVE) {
            throw ApplicationException(
                code = CodeEnum.FRS_003,
                message = "이미 가입된 유저입니다."
            )
        }

        // 2. 삭제된 계정인 경우 -> 재가입 대기 기간 체크
        if (account.status == AccountStatus.INACTIVE && account.deletedAt != null) {
            val daysRemaining = calculateRemainingDays(account.deletedAt!!)

            if (daysRemaining > 0) {
                throw ApplicationException(
                    code = CodeEnum.FRS_003,
                    message = "탈퇴한 유저로 ${daysRemaining}일 후에 재가입이 가능합니다."
                )
            }
        }
    }

    /**
     * 재가입 대기 기간 남은 일수 계산
     * @param deletedAt 계정 삭제 시각
     * @return 남은 일수 (0 이하면 재가입 가능)
     */
    private fun calculateRemainingDays(deletedAt: LocalDateTime): Long {
        val reSignupAvailableDate = deletedAt.plusDays(reSignupWaitingDays.toLong())
        val now = LocalDateTime.now()
        return ChronoUnit.DAYS.between(now, reSignupAvailableDate)
    }
}