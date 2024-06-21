package com.andreformento.sociallogin

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/", produces = ["application/json"])
class UserController {

    @GetMapping
    suspend fun getCurrentUser(authentication: OAuth2AuthenticationToken):Any {
        val user = authentication.principal as DefaultOidcUser
        return object {
            val name = user.userInfo.fullName
            val email = user.email
        }
    }

}
