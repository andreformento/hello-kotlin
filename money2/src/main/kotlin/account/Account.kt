package com.andreformento.money.account

import com.andreformento.money.organization.OrganizationId
import java.util.*

typealias AccountId = UUID

fun String.toAccountId(): AccountId {
    return AccountId.fromString(this)
}

data class Account(
    val id: AccountId,
    val name: String,
    val organizationId: OrganizationId,
    val activated: Boolean,
)

data class AccountRegister(
    val name: String,
)
