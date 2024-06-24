package com.andreformento.money.user

import java.util.*

typealias UserId = UUID

fun String.toUserId(): UserId {
    return UserId.fromString(this)
}

data class User(
    val id: UserId,
    val name: String,
    val email: String,
)

data class UserRegister(
    val name: String,
    val email: String,
)

class UserSignupCreation(
    val name: String,
    val email: String,
)
