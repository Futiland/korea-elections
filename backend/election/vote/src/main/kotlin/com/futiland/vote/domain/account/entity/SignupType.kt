package com.futiland.vote.domain.account.entity

enum class SignupType(
    val description: String,
    val requiresPassword: Boolean
) {
    PHONE_PASSWORD("전화번호 + 비밀번호", true),
    KAKAO("카카오 간편 로그인", false),
    NAVER("네이버 간편 로그인", false),
    GOOGLE("구글 간편 로그인", false);

    fun isSocialLogin(): Boolean = !requiresPassword
}
