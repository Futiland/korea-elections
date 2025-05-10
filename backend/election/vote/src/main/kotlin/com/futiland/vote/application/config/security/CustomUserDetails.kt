package com.futiland.vote.application.config.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(
    val user: CustomUserDto
) : UserDetails {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_USER")).toMutableList()
    }


    override fun getPassword(): String {
        return ""
    }

    override fun getUsername(): String {
        return user.accountId.toString()
    }


}

data class CustomUserDto(
    val accountId: Long = 1L,
)