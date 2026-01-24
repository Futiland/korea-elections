package com.futiland.vote.domain.account.entity

enum class OAuthProvider(val providerName: String) {
    KAKAO("kakao"),
    NAVER("naver"),
    GOOGLE("google");

    companion object {
        fun fromProviderName(name: String): OAuthProvider {
            return values().find { it.providerName.equals(name, ignoreCase = true) }
                ?: throw IllegalArgumentException("지원하지 않는 OAuth Provider: $name")
        }
    }
}
