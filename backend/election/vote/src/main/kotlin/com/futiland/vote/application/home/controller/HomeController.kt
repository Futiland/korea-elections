package com.futiland.vote.application.home.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/")
class HomeController {
    @GetMapping("")
    fun home(): String {
        return "about"  // templates/about.html 이 렌더링됩니다.
    }
}