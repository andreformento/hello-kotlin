package com.andreformento.money.organization.share.repository

import com.andreformento.money.organization.Organization
import com.andreformento.money.organization.OrganizationId
import com.andreformento.money.organization.share.*
import com.andreformento.money.user.User
import com.andreformento.money.user.UserId
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("organization_share")
data class OrganizationShareEntity(
    @Id val id: OrganizationShareId,
    @Column("user_id") val userId: UserId,
    @Column("organization_id") val organizationId: OrganizationId,
    @Column("organization_role") val role: String
) {

    constructor(organizationShareRegister: OrganizationShareRegister) : this(
        id = UUID.randomUUID(),
        userId = organizationShareRegister.userId,
        organizationId = organizationShareRegister.organizationId,
        role = organizationShareRegister.role.toString().lowercase(),
    )

    fun toCreated(): OrganizationShared =
        OrganizationShared(
            id = this.id,
            organizationId = this.organizationId,
            userId = this.userId,
            role = Role.valueOf(this.role.uppercase())
        )

}

@Table("organization_share")
data class FullOrganizationRoleEntity(
    @Id val id: OrganizationShareId,
    @Column("organization_id") val organizationId: OrganizationId,
    @Column("organization_name") val organizationName: String,
    @Column("organization_description") val organizationDescription: String,
    @Column("user_id") val userId: UserId,
    @Column("user_name") val userName: String,
    @Column("user_email") val userEmail: String,
    @Column("organization_role") val role: String
) {

    fun toModel(): OrganizationShare =
        OrganizationShare(
            id = this.id,
            organization = Organization(
                id = organizationId,
                name = organizationName,
                description = organizationDescription,
            ),
            user = User(
                id = userId,
                name = userName,
                email = userEmail,
            ),
            role = Role.valueOf(this.role.uppercase())
        )

}
