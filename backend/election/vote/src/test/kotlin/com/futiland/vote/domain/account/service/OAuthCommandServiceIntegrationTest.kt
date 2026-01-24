package com.futiland.vote.domain.account.service

import com.futiland.vote.application.exception.ApplicationException
import com.futiland.vote.domain.account.entity.Account
import com.futiland.vote.domain.account.entity.Gender
import com.futiland.vote.domain.account.entity.OAuthProvider
import com.futiland.vote.domain.account.entity.OAuthState
import com.futiland.vote.domain.account.entity.SignupType
import com.futiland.vote.domain.account.dto.oauth.OAuthTokenResponse
import com.futiland.vote.domain.account.dto.oauth.OAuthUserInfoResponse
import com.futiland.vote.domain.account.port.out.OAuthProviderPort
import com.futiland.vote.domain.account.repository.AccountRepository
import com.futiland.vote.domain.account.repository.OAuthStateRepository
import com.futiland.vote.domain.account.repository.SocialAccountRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.given
import org.mockito.ArgumentMatchers.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@SpringBootTest(
    properties = [
        "spring.jpa.properties.javax.persistence.schema-generation.create-source=metadata",
        "spring.jpa.properties.javax.persistence.schema-generation.drop-source=metadata",
        "spring.jpa.properties.javax.persistence.schema-generation.database.action=drop-and-create",
        "spring.jpa.show-sql=false"
    ]
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@ActiveProfiles("h2-test")
class OAuthCommandServiceIntegrationTest {

    @Autowired
    private lateinit var oAuthCommandUseCase: OAuthCommandUseCase

    @Autowired
    private lateinit var accountRepository: AccountRepository

    @Autowired
    private lateinit var socialAccountRepository: SocialAccountRepository

    @Autowired
    private lateinit var oAuthStateRepository: OAuthStateRepository

    @MockBean
    private lateinit var kakaoOAuthPort: OAuthProviderPort

    @Test
    fun `카카오 로그인 URL 생성 성공`() {
        // when
        val url = oAuthCommandUseCase.getOAuthLoginUrl(
            provider = OAuthProvider.KAKAO,
            redirectUri = "http://localhost:8080/callback"
        )

        // then
        assertThat(url).contains("https://kauth.kakao.com/oauth/authorize")
        assertThat(url).contains("client_id=")
        assertThat(url).contains("state=")
    }

    @Test
    fun `카카오 로그인 성공 - 신규 사용자`() {
        // given: Mock 카카오 API
        setupKakaoMock(ci = "NEW_CI_123")

        // State 저장
        val state = oAuthStateRepository.save(
            OAuthState(state = "test_state", provider = OAuthProvider.KAKAO)
        )

        // when
        val response = oAuthCommandUseCase.handleOAuthCallback(
            provider = OAuthProvider.KAKAO,
            code = "test_code",
            state = state.state,
            redirectUri = "http://localhost:8080/callback"
        )

        // then
        assertThat(response.isNewUser).isTrue()
        assertThat(response.token).isNotBlank()

        // Account 생성 확인
        val account = accountRepository.findByCi("NEW_CI_123")
        assertThat(account).isNotNull()
        assertThat(account!!.signupType).isEqualTo(SignupType.KAKAO)

        // SocialAccount 생성 확인
        val socialAccount = socialAccountRepository.findByProviderAndProviderAccountId(
            OAuthProvider.KAKAO, "12345678"
        )
        assertThat(socialAccount).isNotNull()
        assertThat(socialAccount!!.accountId).isEqualTo(account.id)
    }

    @Test
    fun `카카오 로그인 성공 - 기존 사용자 (CI 일치)`() {
        // given: 기존 Account
        val existingAccount = Account.create(
            name = "홍길동",
            phoneNumber = "01012345678",
            password = "password",
            gender = Gender.MALE,
            birthDate = LocalDate.of(1990, 1, 1),
            ci = "EXISTING_CI_456"
        )
        accountRepository.save(existingAccount)

        // Mock 카카오 API (동일한 CI)
        setupKakaoMock(ci = "EXISTING_CI_456")

        // State 저장
        val state = oAuthStateRepository.save(
            OAuthState(state = "test_state", provider = OAuthProvider.KAKAO)
        )

        // when
        val response = oAuthCommandUseCase.handleOAuthCallback(
            provider = OAuthProvider.KAKAO,
            code = "test_code",
            state = state.state,
            redirectUri = "http://localhost:8080/callback"
        )

        // then
        assertThat(response.isNewUser).isFalse()
        assertThat(response.token).isNotBlank()

        // 새로운 Account 생성되지 않음
        val accounts = accountRepository.findAllPaged(0, 10)
        assertThat(accounts).hasSize(1)
    }

    @Test
    fun `State 검증 실패 - 유효하지 않은 State`() {
        // given: Mock 카카오 API
        setupKakaoMock(ci = "CI_123")

        // when & then
        assertThrows<ApplicationException> {
            oAuthCommandUseCase.handleOAuthCallback(
                provider = OAuthProvider.KAKAO,
                code = "test_code",
                state = "invalid_state",  // DB에 없는 State
                redirectUri = "http://localhost:8080/callback"
            )
        }
    }

    @Test
    fun `State 검증 실패 - 만료된 State`() {
        // given: 만료된 State
        val expiredState = OAuthState(
            state = "expired_state",
            provider = OAuthProvider.KAKAO,
            expiresAt = LocalDateTime.now().minusMinutes(1)  // 1분 전 만료
        )
        oAuthStateRepository.save(expiredState)

        // Mock 카카오 API
        setupKakaoMock(ci = "CI_123")

        // when & then
        assertThrows<ApplicationException> {
            oAuthCommandUseCase.handleOAuthCallback(
                provider = OAuthProvider.KAKAO,
                code = "test_code",
                state = expiredState.state,
                redirectUri = "http://localhost:8080/callback"
            )
        }
    }

    @Test
    fun `가입 조건 검증 실패 - 재가입 대기 기간`() {
        // given: 삭제된 계정 (재가입 대기 중)
        val deletedAccount = Account.create(
            name = "홍길동",
            phoneNumber = "01012345678",
            password = "password",
            gender = Gender.MALE,
            birthDate = LocalDate.of(1990, 1, 1),
            ci = "DELETED_CI_789"
        )
        deletedAccount.delete()  // 삭제 처리
        accountRepository.save(deletedAccount)

        // Mock 카카오 API (동일한 CI)
        setupKakaoMock(ci = "DELETED_CI_789")

        // State 저장
        val state = oAuthStateRepository.save(
            OAuthState(state = "test_state", provider = OAuthProvider.KAKAO)
        )

        // when & then
        assertThrows<ApplicationException> {
            oAuthCommandUseCase.handleOAuthCallback(
                provider = OAuthProvider.KAKAO,
                code = "test_code",
                state = state.state,
                redirectUri = "http://localhost:8080/callback"
            )
        }
    }

    private fun setupKakaoMock(ci: String) {
        given(kakaoOAuthPort.getProvider()).willReturn(OAuthProvider.KAKAO)

        given(kakaoOAuthPort.getAccessToken(any(String::class.java), any(String::class.java))).willReturn(
            OAuthTokenResponse(
                accessToken = "mock_access_token",
                refreshToken = "mock_refresh_token",
                accessTokenExpiresIn = 3600,
                refreshTokenExpiresIn = 86400,
                scope = null
            )
        )

        given(kakaoOAuthPort.getUserInfo(any(String::class.java))).willReturn(
            OAuthUserInfoResponse(
                id = "12345678",
                name = "홍길동",
                phoneNumber = "01012345678",
                gender = Gender.MALE,
                birthDate = LocalDate.of(1990, 1, 1),
                ci = ci
            )
        )
    }
}
