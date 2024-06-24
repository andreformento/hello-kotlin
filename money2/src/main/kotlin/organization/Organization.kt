package com.andreformento.money.organization

import java.util.*

typealias OrganizationId = UUID

fun String.toOrganizationId(): OrganizationId {
    return OrganizationId.fromString(this)
}

data class Organization(
    val id: OrganizationId,
    val name: String,
    val description: String
)

data class OrganizationRegister(
    val name: String,
    val description: String
)
