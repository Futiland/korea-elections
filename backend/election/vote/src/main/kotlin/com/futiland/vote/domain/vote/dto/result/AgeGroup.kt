package com.futiland.vote.domain.vote.dto.result

enum class AgeGroup(val value: String, val age:Int) {
    AGE_10("10대", 10),
    AGE_20("20대", 20),
    AGE_30("30대", 30),
    AGE_40("40대", 40),
    AGE_50("50대", 50),
    AGE_60("60대", 60),
    AGE_70("70대 이상", 70);

    companion object {
        fun fromAge(age: Int): AgeGroup {
            return when (age) {
                in 10..19 -> AGE_10
                in 20..29 -> AGE_20
                in 30..39 -> AGE_30
                in 40..49 -> AGE_40
                in 50..59 -> AGE_50
                in 60..69 -> AGE_60
                else -> AGE_70
            }
        }
    }
}