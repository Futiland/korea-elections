package com.futiland.vote

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class VoteApplication

fun main(args: Array<String>) {
    runApplication<VoteApplication>(*args)
}
