package com.andreformento.money.organization.repository

import com.andreformento.money.organization.Organization
import com.andreformento.money.organization.OrganizationId
import com.andreformento.money.organization.OrganizationRegister
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("organization")
data class OrganizationEntity(
    @Id val id: OrganizationId? = null,
    @Column("name") val name: String,
    @Column("description") val description: String
) {
    constructor(organizationRegister: OrganizationRegister) : this(
        name = organizationRegister.name,
        description = organizationRegister.description
    )

    fun toModel(): Organization {
        return Organization(this.id!!, this.name, this.description)
    }
}
