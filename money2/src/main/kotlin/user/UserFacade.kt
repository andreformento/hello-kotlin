package com.andreformento.money.user

import com.andreformento.money.user.repository.Users
import org.springframework.stereotype.Service

@Service
class UserFacade(private val users: Users) {

    suspend fun create(userRegister: UserRegister): User {
        // TODO authorization
        return users.create(userRegister)
    }

    suspend fun findByEmail(email: String): User? {
        // TODO authorization
        return users.findByEmail(email)
    }

}
