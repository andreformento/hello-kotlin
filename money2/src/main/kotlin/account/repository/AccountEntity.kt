package com.andreformento.money.account.repository

import com.andreformento.money.account.Account
import com.andreformento.money.account.AccountId
import com.andreformento.money.account.AccountRegister
import com.andreformento.money.organization.OrganizationId
import com.andreformento.money.organization.toOrganizationId
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("account")
data class AccountEntity(
    @Id val id: AccountId? = null,
    @Column("name") val name: String,
    @Column("organization_id") val organizationId: OrganizationId,
    @Column("activated") val activated: Boolean,
) {
    constructor(organizationId: OrganizationId, accountRegister: AccountRegister) : this(
        name = accountRegister.name,
        organizationId = organizationId,
        activated = true
    )

    fun toModel(): Account {
        return Account(
            id=this.id!!,
            name=this.name,
            organizationId = this.organizationId,
            activated=this.activated,
        )
    }
}
