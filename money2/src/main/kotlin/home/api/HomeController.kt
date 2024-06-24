package com.andreformento.money.home.api

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class HomeController {

    @GetMapping
    suspend fun home(@AuthenticationPrincipal principal: OAuth2User): Any {
        return object {
            val name = principal.attributes["name"]
        }
    }

}
