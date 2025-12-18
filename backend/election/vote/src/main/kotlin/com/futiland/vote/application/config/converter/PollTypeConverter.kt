package com.futiland.vote.application.config.converter

import com.futiland.vote.domain.poll.entity.PollType
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

/**
 * URL 경로의 소문자 문자열을 PollType enum으로 변환하는 Converter
 *
 * 사용 예:
 * - /poll/v1/public -> PollType.PUBLIC
 * - /poll/v1/system -> PollType.SYSTEM
 */
@Component
class PollTypeConverter : Converter<String, PollType> {

    override fun convert(source: String): PollType {
        return PollType.valueOf(source.uppercase())
    }
}
