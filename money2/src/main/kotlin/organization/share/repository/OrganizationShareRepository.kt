package com.andreformento.money.organization.share.repository

import com.andreformento.money.organization.OrganizationId
import com.andreformento.money.organization.share.OrganizationShareId
import com.andreformento.money.user.UserId
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
internal interface OrganizationShareRepository : CoroutineCrudRepository<OrganizationShareEntity, OrganizationShareId> {

    @Modifying
    @Query(
        """INSERT INTO organization_share ( id,  user_id,  organization_id,  organization_role) VALUES
                                            (:id, :user_id, :organization_id, cast(:organization_role as role_description))"""
    )
    suspend fun save(
        @Param("id") organizationShareId: OrganizationShareId,
        @Param("user_id") userId: UserId,
        @Param("organization_id") organizationId: OrganizationId,
        @Param("organization_role") organizationRole: String,
    )

    @Modifying
    @Query("delete from organization_share where user_id = :user_id and organization_id = :organization_id and id = :organization_share_id")
    suspend fun delete(
        @Param("user_id") userId: UserId,
        @Param("organization_id") organizationId: OrganizationId,
        @Param("organization_share_id") organizationShareId: OrganizationShareId,
    ): Int

}
