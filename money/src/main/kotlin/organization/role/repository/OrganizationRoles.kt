package com.andreformento.money.organization.role.repository

import com.andreformento.money.organization.OrganizationId
import com.andreformento.money.organization.role.OrganizationRoleCreated
import com.andreformento.money.organization.role.OrganizationRoleCreation
import com.andreformento.money.user.UserId
import org.springframework.stereotype.Repository

@Repository
class OrganizationRoles internal constructor(private val organizationRoleRepository: OrganizationRoleRepository) {

    suspend fun save(organizationCreation: OrganizationRoleCreation): OrganizationRoleCreated =
        OrganizationRoleEntity(organizationCreation)
            .also {
                organizationRoleRepository.save(
                    organizationRoleId = it.id,
                    organizationId = it.organizationId,
                    userId = it.userId,
                    organizationRole = it.organizationRole,
                )
            }
            .toCreated()

    suspend fun delete(userId: UserId, organizationId: OrganizationId) =
        organizationRoleRepository.delete(userId, organizationId)

}