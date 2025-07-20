package com.futiland.vote.domain.account.service

import com.futiland.vote.application.common.httpresponse.CodeEnum
import com.futiland.vote.application.exception.ApplicationException
import com.futiland.vote.domain.account.dto.response.VerifiedResponse
import com.futiland.vote.domain.account.port.out.IdentityVerificationPort
import com.futiland.vote.domain.account.repository.AccountRepository
import org.springframework.stereotype.Service

@Service
class MobileIdentifyService(
    private val identityVerificationPort: IdentityVerificationPort,
    private val accountRepository: AccountRepository,
) : MobileIdentifyUseCase {
    override fun verify(identityVerificationId: String): VerifiedResponse {
        val verificationResponse = identityVerificationPort.verify(identityVerificationId)
        val account = accountRepository.findByCi(ci = verificationResponse.id)
            ?: throw ApplicationException(
                code = CodeEnum.FRS_001,
                message = "해당 가입된 사용자가 없습니다. 가입을 먼저 진행해주세요."
            )
        // TODO : 여기서 번호 변경이 발생할 수 있으므로, 기존 번호와 비교하여 변경된 경우 업데이트 로직 추가 필요
        return VerifiedResponse(
            id = account.id,
            name = account.name,
            phoneNumber = account.phoneNumber,
        )
    }
}