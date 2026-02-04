package com.futiland.vote.application.common

import com.futiland.vote.application.common.httpresponse.CodeEnum
import com.futiland.vote.application.exception.ApplicationException
import java.net.URI

object OAuthUrlValidator {
    /**
     * URL의 호스트(프로토콜 + 도메인 + 포트)가 화이트리스트에 포함되어 있는지 검증합니다.
     * 경로(path)는 검증하지 않아 유연하게 사용 가능합니다.
     *
     * @param url 검증할 전체 URL (예: "http://localhost:3000/custom/path")
     * @param whitelist 허용된 호스트 목록 (예: ["http://localhost:3000", "https://korea-election.com"])
     * @throws ApplicationException 호스트가 화이트리스트에 없는 경우
     *
     * 예시:
     * - url: "http://localhost:3000/oauth/callback" → 허용 (호스트: "http://localhost:3000")
     * - url: "http://localhost:3000/custom/path" → 허용 (호스트: "http://localhost:3000")
     * - url: "https://korea-election.com/auth/success" → 허용 (호스트: "https://korea-election.com")
     * - url: "http://malicious.com/oauth/callback" → 거부 (호스트가 화이트리스트에 없음)
     */
    fun validateHostWhitelist(url: String, whitelist: List<String>) {
        try {
            val uri = URI(url)
            val urlHost = buildHost(uri)

            // 화이트리스트의 각 항목에서 호스트 부분만 추출하여 비교
            val whitelistHosts = whitelist.map { whitelistUrl ->
                val whitelistUri = URI(whitelistUrl)
                buildHost(whitelistUri)
            }

            if (!whitelistHosts.contains(urlHost)) {
                throw ApplicationException(
                    code = CodeEnum.FRS_003,
                    message = "허용되지 않은 프론트엔드 URL입니다. 호스트: $urlHost"
                )
            }
        } catch (e: ApplicationException) {
            throw e
        } catch (e: Exception) {
            throw ApplicationException(
                code = CodeEnum.FRS_003,
                message = "유효하지 않은 URL 형식입니다: $url"
            )
        }
    }

    /**
     * URI에서 호스트 부분(프로토콜 + 호스트 + 포트)을 추출합니다.
     * 예: "http://localhost:3000/path" → "http://localhost:3000"
     */
    private fun buildHost(uri: URI): String {
        val scheme = uri.scheme ?: throw IllegalArgumentException("URL에 프로토콜이 없습니다")
        val host = uri.host ?: throw IllegalArgumentException("URL에 호스트가 없습니다")
        val port = if (uri.port != -1) ":${uri.port}" else ""
        return "$scheme://$host$port"
    }
}
