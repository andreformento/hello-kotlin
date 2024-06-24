package com.andreformento.money.organization.repository

import com.andreformento.money.organization.Organization
import com.andreformento.money.organization.OrganizationId
import com.andreformento.money.organization.OrganizationRegister
import com.andreformento.money.user.security.CurrentUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Repository

@Repository
class Organizations internal constructor(private val organizationRepository: OrganizationRepository) {

    suspend fun findAllFromUser(currentUser: CurrentUser): Flow<Organization> =
        organizationRepository.findAllFromUser(userId = currentUser.id).map(OrganizationEntity::toModel)

    suspend fun save(organizationRegister: OrganizationRegister): Organization =
        organizationRepository.save(OrganizationEntity(organizationRegister)).toModel()

    suspend fun update(organization: Organization): Organization =
        organizationRepository.save(
            OrganizationEntity(
                id = organization.id,
                name = organization.name,
                description = organization.description,
            )
        ).toModel()

    suspend fun deleteById(id: OrganizationId) =
        organizationRepository.deleteById(id)

}
