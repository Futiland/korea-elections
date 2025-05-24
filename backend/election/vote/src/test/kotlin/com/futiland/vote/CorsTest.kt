package com.futiland.vote

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2-test")
class CorsTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `CORS preflight 요청 허용 여부 테스트`() {
        mockMvc.perform(
            options("/api/hello") // 테스트할 엔드포인트
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "GET")
        )
            .andExpect(status().isOk)
            .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
    }

    @ParameterizedTest
    @CsvSource(
        "http://localhost:3000, true",
        "http://dev.korea-election.com, true",
        "http://unauthorized-origin.com, false"
    )
    fun `여러 Origin에 대한 CORS 테스트`(origin: String, isAllowed: Boolean) {
        val result = mockMvc.perform(
            options("/api/hello")
                .header("Origin", origin)
                .header("Access-Control-Request-Method", "GET")
        )

        if (isAllowed) {
            result.andExpect(status().isOk)
                .andExpect(header().string("Access-Control-Allow-Origin", origin))
        } else {
            result.andExpect(status().isForbidden)
        }
    }
}