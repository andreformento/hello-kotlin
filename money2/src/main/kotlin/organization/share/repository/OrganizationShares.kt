package com.andreformento.money.organization.share.repository

import com.andreformento.money.organization.OrganizationId
import com.andreformento.money.organization.share.OrganizationShare
import com.andreformento.money.organization.share.OrganizationShareId
import com.andreformento.money.organization.share.OrganizationShareRegister
import com.andreformento.money.organization.share.OrganizationShared
import com.andreformento.money.user.UserId
import org.springframework.stereotype.Repository

@Repository
class OrganizationShares internal constructor(
    private val organizationShareRepository: OrganizationShareRepository,
    private val fullOrganizationShareRepository: FullOrganizationShareRepository,
) {

    suspend fun save(organizationShareRegister: OrganizationShareRegister): OrganizationShared =
        OrganizationShareEntity(organizationShareRegister)
            .also {
                organizationShareRepository.save(
                    organizationShareId = it.id,
                    organizationId = it.organizationId,
                    userId = it.userId,
                    organizationRole = it.role,
                )
            }
            .toCreated()

    suspend fun delete(userId: UserId, organizationId: OrganizationId, organizationShareId: OrganizationShareId) =
        organizationShareRepository.delete(
            userId = userId,
            organizationId = organizationId,
            organizationShareId = organizationShareId
        )

    suspend fun getUnsafeUserOrganization(userEmail: String, organizationId: OrganizationId): OrganizationShare? =
        fullOrganizationShareRepository
            .getUnsafeUserOrganization(organizationId = organizationId, userEmail = userEmail)
            ?.toModel()

    suspend fun getUnsafeUserOrganization(
        organizationShareId: OrganizationShareId,
        organizationId: OrganizationId
    ): OrganizationShare? =
        fullOrganizationShareRepository
            .getUnsafeUserOrganization(organizationId = organizationId, organizationShareId = organizationShareId)
            ?.toModel()

}
