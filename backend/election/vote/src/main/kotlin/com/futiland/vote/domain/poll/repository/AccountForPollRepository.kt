package com.futiland.vote.domain.poll.repository

import com.futiland.vote.application.poll.dto.response.CreatorInfoResponse

/**
 * Poll 도메인에서 Account 정보를 조회하기 위한 Repository
 * 도메인 간 의존성을 분리하기 위해 별도 인터페이스로 정의
 */
interface AccountForPollRepository {
    /**
     * 단일 계정의 작성자 정보 조회
     */
    fun getCreatorInfoById(accountId: Long): CreatorInfoResponse

    /**
     * 여러 계정의 작성자 정보 조회 (N+1 방지용)
     * @param accountIds 계정 ID 목록
     * @return accountId를 키로 하는 CreatorInfoResponse Map
     */
    fun getCreatorInfoByIds(accountIds: List<Long>): Map<Long, CreatorInfoResponse>
}
