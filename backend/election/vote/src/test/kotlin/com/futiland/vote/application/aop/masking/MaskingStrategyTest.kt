package com.futiland.vote.application.aop.masking

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MaskingStrategyTest {

    @Nested
    inner class NameMaskingStrategyTest {
        private val strategy = NameMaskingStrategy()

        @Test
        fun `3글자 이름은 첫글자만 보이고 나머지 마스킹`() {
            assertThat(strategy.mask("홍길동")).isEqualTo("홍**")
        }

        @Test
        fun `2글자 이름은 첫글자만 보이고 나머지 마스킹`() {
            assertThat(strategy.mask("홍길")).isEqualTo("홍*")
        }

        @Test
        fun `1글자 이름은 그대로 반환`() {
            assertThat(strategy.mask("홍")).isEqualTo("홍")
        }

        @Test
        fun `빈 문자열은 그대로 반환`() {
            assertThat(strategy.mask("")).isEqualTo("")
        }

        @Test
        fun `4글자 이름은 첫글자만 보이고 나머지 마스킹`() {
            assertThat(strategy.mask("남궁민수")).isEqualTo("남***")
        }

        @Test
        fun `영문 이름도 마스킹 적용`() {
            assertThat(strategy.mask("John")).isEqualTo("J***")
        }
    }

    @Nested
    inner class MiddleMaskingStrategyTest {
        private val strategy = MiddleMaskingStrategy()

        @Test
        fun `3글자 이름은 첫글자와 마지막 글자만 보임`() {
            assertThat(strategy.mask("홍길동")).isEqualTo("홍*동")
        }

        @Test
        fun `4글자 이름은 첫글자와 마지막 글자만 보임`() {
            assertThat(strategy.mask("남궁민수")).isEqualTo("남**수")
        }

        @Test
        fun `2글자 이름은 그대로 반환`() {
            assertThat(strategy.mask("홍길")).isEqualTo("홍길")
        }

        @Test
        fun `1글자 이름은 그대로 반환`() {
            assertThat(strategy.mask("홍")).isEqualTo("홍")
        }

        @Test
        fun `빈 문자열은 그대로 반환`() {
            assertThat(strategy.mask("")).isEqualTo("")
        }
    }

    @Nested
    inner class FullMaskingStrategyTest {
        private val strategy = FullMaskingStrategy()

        @Test
        fun `모든 글자가 마스킹됨`() {
            assertThat(strategy.mask("홍길동")).isEqualTo("***")
        }

        @Test
        fun `빈 문자열은 그대로 반환`() {
            assertThat(strategy.mask("")).isEqualTo("")
        }
    }

    @Nested
    inner class MaskingTypeTest {
        @Test
        fun `NAME 타입은 NameMaskingStrategy 반환`() {
            val strategy = MaskingType.NAME.getStrategy()
            assertThat(strategy).isInstanceOf(NameMaskingStrategy::class.java)
        }

        @Test
        fun `MIDDLE 타입은 MiddleMaskingStrategy 반환`() {
            val strategy = MaskingType.MIDDLE.getStrategy()
            assertThat(strategy).isInstanceOf(MiddleMaskingStrategy::class.java)
        }

        @Test
        fun `FULL 타입은 FullMaskingStrategy 반환`() {
            val strategy = MaskingType.FULL.getStrategy()
            assertThat(strategy).isInstanceOf(FullMaskingStrategy::class.java)
        }
    }
}
