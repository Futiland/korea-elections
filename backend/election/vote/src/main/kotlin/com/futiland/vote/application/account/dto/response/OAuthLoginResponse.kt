package com.futiland.vote.application.account.dto.response

data class OAuthLoginResponse(
    val token: String,
    /**
     * 신규 가입 여부
     * - true: 이번 OAuth 로그인으로 신규 계정 생성됨
     * - false: 기존 계정으로 로그인됨
     *
     * 활용 사례:
     * - 온보딩/튜토리얼 표시
     * - 웰컴 메시지 ("처음 오셨네요!" vs "다시 오신 걸 환영합니다!")
     * - 신규 가입자 대상 마케팅 (쿠폰, 포인트 지급)
     * - 분석/트래킹 (신규 가입 vs 재방문 구분)
     */
    val isNewUser: Boolean,
    /**
     * 프론트엔드 리다이렉트 URL
     * - OAuth 완료 후 프론트엔드가 지정한 리다이렉트 URL
     * - State 기반으로 저장된 값 반환
     */
    val frontendRedirectUrl: String
)
