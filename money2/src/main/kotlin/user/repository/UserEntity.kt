package com.andreformento.money.user.repository

import com.andreformento.money.user.User
import com.andreformento.money.user.UserId
import com.andreformento.money.user.UserRegister
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("users")
data class UserEntity(
    @Id val id: UserId? = null,
    @Column("name") val name: String,
    @Column("email") val email: String
) {
    constructor(userRegister: UserRegister) : this(name = userRegister.name, email = userRegister.email)

    fun toModel(): User {
        return User(this.id!!, this.name, this.email)
    }
}
