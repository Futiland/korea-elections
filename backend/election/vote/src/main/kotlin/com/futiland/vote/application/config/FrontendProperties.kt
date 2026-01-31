package com.futiland.vote.application.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * 프론트엔드 URL 설정
 *
 * application.yaml에서 관리됩니다:
 * ```yaml
 * frontend:
 *   urls:
 *     - http://localhost:3000
 *     - https://dev.korea-election.com
 * ```
 *
 * Spring의 @ConfigurationProperties가 yaml의 리스트를 자동으로 바인딩합니다.
 * 환경별(dev, prod)로 프로파일에서 오버라이드 가능합니다.
 */
@Component
@ConfigurationProperties(prefix = "frontend")
class FrontendProperties {
    /**
     * OAuth 로그인 완료 후 리다이렉트할 프론트엔드 URL 목록
     * 첫 번째 URL이 기본 리다이렉트 주소로 사용됩니다.
     */
    var urls: List<String> = emptyList()
}
