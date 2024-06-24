package com.andreformento.money.user.repository

import com.andreformento.money.user.User
import com.andreformento.money.user.UserRegister
import org.springframework.stereotype.Repository

@Repository
class Users internal constructor(private val userRepository: UserRepository) {

    suspend fun findByEmail(email: String): User? {
        // TODO send metric user not found by email
        return userRepository.findByEmail(email)?.toModel()
    }

    suspend fun create(userRegister: UserRegister): User {
        return userRepository.save(UserEntity(userRegister)).toModel()
    }

}
