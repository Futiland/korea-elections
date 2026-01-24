package com.futiland.vote.domain.account.service

import com.futiland.vote.application.account.dto.response.OAuthLoginResponse
import com.futiland.vote.application.exception.ApplicationException
import com.futiland.vote.application.common.httpresponse.CodeEnum
import com.futiland.vote.domain.account.dto.AccountJwtPayload
import com.futiland.vote.domain.account.entity.*
import com.futiland.vote.domain.account.port.out.OAuthProviderPort
import com.futiland.vote.domain.account.repository.AccountRepository
import com.futiland.vote.domain.account.repository.OAuthStateRepository
import com.futiland.vote.domain.account.repository.SocialAccountRepository
import com.futiland.vote.domain.common.JwtTokenProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OAuthCommandService(
    private val oAuthStateRepository: OAuthStateRepository,
    private val accountRepository: AccountRepository,
    private val socialAccountRepository: SocialAccountRepository,
    private val accountQueryUseCase: AccountQueryUseCase,
    private val jwtTokenProvider: JwtTokenProvider,
    @Value("\${access_token.ttl}")
    private val accessTokenTtl: Int,
    oAuthProviderPorts: List<OAuthProviderPort>
) : OAuthCommandUseCase {

    private val providerPortMap: Map<OAuthProvider, OAuthProviderPort> =
        oAuthProviderPorts.associateBy { it.getProvider() }

    @Transactional
    override fun getOAuthLoginUrl(provider: OAuthProvider, redirectUri: String): String {
        val port = providerPortMap[provider]
            ?: throw ApplicationException(
                code = CodeEnum.FRS_003,
                message = "지원하지 않는 OAuth Provider입니다: ${provider.providerName}"
            )

        // State 생성 및 저장
        val oAuthState = OAuthState(provider = provider)
        oAuthStateRepository.save(oAuthState)

        // Authorization URL 생성
        return port.getAuthorizationUrl(oAuthState.state, redirectUri)
    }

    @Transactional
    override fun handleOAuthCallback(
        provider: OAuthProvider,
        code: String,
        state: String,
        redirectUri: String
    ): OAuthLoginResponse {
        val port = providerPortMap[provider]
            ?: throw ApplicationException(
                code = CodeEnum.FRS_003,
                message = "지원하지 않는 OAuth Provider입니다: ${provider.providerName}"
            )

        // State 검증
        validateState(state, provider)

        // Access Token 발급
        val tokenResponse = port.getAccessToken(code, redirectUri)

        // 사용자 정보 조회
        val userInfo = port.getUserInfo(tokenResponse.accessToken)

        // CI로 기존 ACTIVE 계정 확인 (삭제된 계정 제외)
        val existingAccount = accountRepository.findActiveByCi(userInfo.ci)

        return if (existingAccount != null) {
            // 기존 사용자 로그인
            handleExistingUserLogin(existingAccount, provider, userInfo.id, tokenResponse)
        } else {
            // 신규 사용자 가입
            handleNewUserSignup(userInfo, provider, tokenResponse)
        }
    }

    private fun validateState(state: String, provider: OAuthProvider) {
        val oAuthState = oAuthStateRepository.findByState(state)
            ?: throw ApplicationException(
                code = CodeEnum.FRS_003,
                message = "유효하지 않은 State 값입니다."
            )

        if (oAuthState.isExpired()) {
            throw ApplicationException(
                code = CodeEnum.FRS_003,
                message = "만료된 State 값입니다."
            )
        }

        if (oAuthState.provider != provider) {
            throw ApplicationException(
                code = CodeEnum.FRS_003,
                message = "Provider가 일치하지 않습니다."
            )
        }

        // 1회용 State 삭제
        oAuthStateRepository.deleteByState(state)
    }

    private fun getAccountJwtPayload(account: Account): Map<String, Any> {
        return AccountJwtPayload(
            accountId = account.id,
        ).toMap()
    }

    private fun handleExistingUserLogin(
        account: Account,
        provider: OAuthProvider,
        providerAccountId: String,
        tokenResponse: com.futiland.vote.domain.account.dto.oauth.OAuthTokenResponse
    ): OAuthLoginResponse {
        // 1. ACTIVE 상태의 소셜 계정 확인
        val activeSocialAccount = socialAccountRepository.findActiveByProviderAndProviderAccountId(
            provider, providerAccountId
        )

        if (activeSocialAccount != null) {
            // ACTIVE 상태 -> 토큰만 업데이트
            activeSocialAccount.updateTokens(
                accessToken = tokenResponse.accessToken,
                refreshToken = tokenResponse.refreshToken,
                accessTokenExpiresAt = tokenResponse.getAccessTokenExpiresAt(),
                refreshTokenExpiresAt = tokenResponse.getRefreshTokenExpiresAt()
            )
            socialAccountRepository.save(activeSocialAccount)
        } else {
            // 2. 비활성화된 소셜 계정이 있는지 확인 (모든 상태 검색)
            val inactiveSocialAccount = socialAccountRepository.findByProviderAndProviderAccountId(
                provider, providerAccountId
            )

            if (inactiveSocialAccount != null) {
                // INACTIVE 상태 -> 재활성화
                inactiveSocialAccount.reactivate(
                    accessToken = tokenResponse.accessToken,
                    refreshToken = tokenResponse.refreshToken,
                    accessTokenExpiresAt = tokenResponse.getAccessTokenExpiresAt(),
                    refreshTokenExpiresAt = tokenResponse.getRefreshTokenExpiresAt()
                )
                socialAccountRepository.save(inactiveSocialAccount)
            } else {
                // 3. 소셜 계정이 아예 없음 -> 신규 생성
                val socialAccount = SocialAccount.create(
                    accountId = account.id,
                    provider = provider,
                    providerAccountId = providerAccountId,
                    accessToken = tokenResponse.accessToken,
                    refreshToken = tokenResponse.refreshToken,
                    accessTokenExpiresAt = tokenResponse.getAccessTokenExpiresAt(),
                    refreshTokenExpiresAt = tokenResponse.getRefreshTokenExpiresAt()
                )
                socialAccountRepository.save(socialAccount)
            }
        }

        // JWT 토큰 발급
        val payload = getAccountJwtPayload(account)
        val jwtToken = jwtTokenProvider.generateToken(
            payload = payload,
            ttl = accessTokenTtl
        )

        return OAuthLoginResponse(
            token = jwtToken,
            isNewUser = false
        )
    }

    private fun handleNewUserSignup(
        userInfo: com.futiland.vote.domain.account.dto.oauth.OAuthUserInfoResponse,
        provider: OAuthProvider,
        tokenResponse: com.futiland.vote.domain.account.dto.oauth.OAuthTokenResponse
    ): OAuthLoginResponse {
        // 가입 조건 검증 (재가입 대기 기간 등)
        accountQueryUseCase.validateSignUpEligibility(userInfo.ci)

        // 신규 Account 생성
        val signupType = when (provider) {
            OAuthProvider.KAKAO -> SignupType.KAKAO
            OAuthProvider.NAVER -> SignupType.NAVER
            OAuthProvider.GOOGLE -> SignupType.GOOGLE
        }

        val account = Account.createSocialAccount(
            name = userInfo.name,
            phoneNumber = userInfo.phoneNumber,
            gender = userInfo.gender,
            birthDate = userInfo.birthDate,
            ci = userInfo.ci,
            signupType = signupType
        )
        val savedAccount = accountRepository.save(account)

        // SocialAccount 처리: 비활성화된 계정이 있으면 재활성화, 없으면 신규 생성
        val existingSocialAccount = socialAccountRepository.findByProviderAndProviderAccountId(
            provider, userInfo.id
        )

        if (existingSocialAccount != null) {
            // 이전에 탈퇴했다가 재가입하는 경우 -> 재활성화
            existingSocialAccount.reactivate(
                accessToken = tokenResponse.accessToken,
                refreshToken = tokenResponse.refreshToken,
                accessTokenExpiresAt = tokenResponse.getAccessTokenExpiresAt(),
                refreshTokenExpiresAt = tokenResponse.getRefreshTokenExpiresAt()
            )
            socialAccountRepository.save(existingSocialAccount)
        } else {
            // 완전히 새로운 소셜 계정 -> 신규 생성
            val socialAccount = SocialAccount.create(
                accountId = savedAccount.id,
                provider = provider,
                providerAccountId = userInfo.id,
                accessToken = tokenResponse.accessToken,
                refreshToken = tokenResponse.refreshToken,
                accessTokenExpiresAt = tokenResponse.getAccessTokenExpiresAt(),
                refreshTokenExpiresAt = tokenResponse.getRefreshTokenExpiresAt()
            )
            socialAccountRepository.save(socialAccount)
        }

        // JWT 토큰 발급
        val payload = getAccountJwtPayload(savedAccount)
        val jwtToken = jwtTokenProvider.generateToken(
            payload = payload,
            ttl = accessTokenTtl
        )

        return OAuthLoginResponse(
            token = jwtToken,
            isNewUser = true
        )
    }
}
