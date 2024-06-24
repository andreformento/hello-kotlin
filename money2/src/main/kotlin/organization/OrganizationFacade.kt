package com.andreformento.money.organization

import com.andreformento.money.organization.repository.Organizations
import com.andreformento.money.organization.share.OrganizationShareRegister
import com.andreformento.money.organization.share.OrganizationShared
import com.andreformento.money.organization.share.Role
import com.andreformento.money.organization.share.repository.OrganizationShares
import com.andreformento.money.user.security.CurrentUser
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

class CreatedOrganization(
    val organization: Organization,
    val organizationRole: OrganizationShared,
)

@Service
class OrganizationFacade(private val organizations: Organizations, private val organizationShares: OrganizationShares) {

    suspend fun getAllFromUser(currentUser: CurrentUser): Flow<Organization> =
        organizations.findAllFromUser(currentUser)

    suspend fun create(currentUser: CurrentUser, organizationRegister: OrganizationRegister): CreatedOrganization =
        organizations
            .save(organizationRegister)
            .let {
                CreatedOrganization(
                    organization = it,
                    organizationRole = organizationShares.save(
                        OrganizationShareRegister(
                            it.id,
                            currentUser.id,
                            Role.OWNER
                        )
                    )
                )
            }

    suspend fun update(organizationId: OrganizationId, organizationRegister: OrganizationRegister): Organization? =
        organizations.update(Organization(organizationId, organizationRegister.name, organizationRegister.description))

    suspend fun delete(organizationId: OrganizationId) =
        organizations.deleteById(organizationId)

}
