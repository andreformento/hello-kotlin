package com.andreformento.money.user.api

import com.andreformento.money.user.User
import com.andreformento.money.user.UserId
import com.andreformento.money.user.security.UserPermissionLoader
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class LoggedUserResponse(val id: UserId, val name: String, val email: String) {
    constructor(user: User) : this(id = user.id, name = user.name, email = user.email)
}

@RestController
@RequestMapping("/user", produces = ["application/json"])
class UserController(
    private val userPermissionLoader: UserPermissionLoader,
) {

    @GetMapping
    suspend fun getCurrentUser(@AuthenticationPrincipal principal: OAuth2User): ResponseEntity<LoggedUserResponse> =
        userPermissionLoader
            .getCurrentUser(principal)
            ?.let { ResponseEntity.ok(LoggedUserResponse(it)) }
            ?: ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()


}
