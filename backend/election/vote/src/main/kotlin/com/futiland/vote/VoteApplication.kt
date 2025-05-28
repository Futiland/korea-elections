package com.futiland.vote

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

//@EnableFeignClients
@SpringBootApplication
class VoteApplication

fun main(args: Array<String>) {
    runApplication<VoteApplication>(*args)
}
