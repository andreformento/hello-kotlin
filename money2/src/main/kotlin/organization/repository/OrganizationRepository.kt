package com.andreformento.money.organization.repository

import com.andreformento.money.organization.OrganizationId
import com.andreformento.money.user.UserId
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
internal interface OrganizationRepository : CoroutineCrudRepository<OrganizationEntity, OrganizationId> {

    @Query(
        """
        SELECT o.*
          FROM organization o
    INNER JOIN organization_share orr on orr.organization_id = o.id
         WHERE orr.user_id = :user_id
        """
    )
    suspend fun findAllFromUser(@Param("user_id") userId: UserId): Flow<OrganizationEntity>

}
