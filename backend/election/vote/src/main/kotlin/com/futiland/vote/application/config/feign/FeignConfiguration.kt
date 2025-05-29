package com.futiland.vote.application.config.feign

import feign.Logger
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.cloud.openfeign.FeignAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ImportAutoConfiguration(FeignAutoConfiguration::class)
@EnableFeignClients(basePackages = ["com.futiland.vote.*"])
class FeignConfiguration(
) {
    @Bean
    fun feignLoggerLevel(): Logger.Level {
        return feign.Logger.Level.FULL
    }
}