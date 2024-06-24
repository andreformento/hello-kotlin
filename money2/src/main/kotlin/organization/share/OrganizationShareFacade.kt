package com.andreformento.money.organization.share

import com.andreformento.money.organization.OrganizationId
import com.andreformento.money.organization.share.repository.OrganizationShares
import com.andreformento.money.user.UserId
import org.springframework.stereotype.Service

@Service
class OrganizationShareFacade(private val organizationShares: OrganizationShares) {

    suspend fun share(userId: UserId, organizationId: OrganizationId): OrganizationShared =
        organizationShares.save(
            OrganizationShareRegister(
                userId = userId,
                organizationId = organizationId,
                role = Role.ADMIN,
            )
        )

    // TODO test!
    suspend fun delete(
        organizationId: OrganizationId,
        organizationShareIdToBeRemoved: OrganizationShareId,
        authenticatedOrganizationShare: OrganizationShare,
    ): OrganizationShareDeleteResult {
        val organizationShareToBeRemoved = organizationShares.getUnsafeUserOrganization(
            organizationShareId = organizationShareIdToBeRemoved,
            organizationId = organizationId
        ) ?: return OrganizationShareNotFoundForThisUser

        val denyReasonForNotDelete = organizationShareToBeRemoved.denyReasonForNotDelete(authenticatedOrganizationShare)

        return if (denyReasonForNotDelete == null) {
            val deletedCount = organizationShares.delete(
                userId = organizationShareToBeRemoved.user.id,
                organizationId = organizationId,
                organizationShareId = organizationShareIdToBeRemoved
            )
            if (deletedCount > 0) OrganizationShareDeleted
            else OrganizationShareNotFound
        } else {
            denyReasonForNotDelete
        }
    }

}
