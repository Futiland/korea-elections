package com.futiland.vote.domain.account.service

import com.futiland.vote.application.account.dto.response.AccountStatsResponse
import com.futiland.vote.application.account.dto.response.ProfileResponse
import com.futiland.vote.application.account.dto.response.StopperResponse
import com.futiland.vote.domain.account.entity.ServiceTarget

interface AccountQueryUseCase {
    fun getProfileById(id: Long) : ProfileResponse
    fun getSignupStopper(): StopperResponse

    /**
     * 회원가입 가능 여부 검증 (중복 가입 체크 + 재가입 대기 기간 검증)
     *
     * **두 가지 검증을 하나의 메서드로 통합한 이유:**
     * - 두 검증 모두 동일한 CI로 Account를 조회해야 함
     * - JPA 1차 캐시는 PK 기반이므로 CI 조회 시 캐시 미작동
     * - 분리 시 매번 DB 조회 발생 (N번 조회 문제)
     * - 통합하여 **단일 DB 조회**로 성능 최적화
     *
     * **검증 순서:**
     * 1. 활성 계정이 존재하면 중복 가입 불가
     * 2. 삭제된 계정이 존재하고 재가입 대기 기간이 남았으면 재가입 불가
     *
     * @param ci 본인인증 CI
     * @throws ApplicationException 중복 가입이거나 재가입 대기 기간이 남은 경우
     */
    fun validateSignUpEligibility(ci: String)

    /**
     * 계정 통계 조회 (내가 만든 투표 수, 참여한 PUBLIC/SYSTEM 투표 수)
     */
    fun getAccountStats(accountId: Long): AccountStatsResponse
}