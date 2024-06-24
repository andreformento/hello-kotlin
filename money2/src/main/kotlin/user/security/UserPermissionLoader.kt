package com.andreformento.money.user.security

import com.andreformento.money.organization.OrganizationId
import com.andreformento.money.organization.share.OrganizationShare
import com.andreformento.money.organization.share.repository.OrganizationShares
import com.andreformento.money.user.User
import com.andreformento.money.user.UserFacade
import com.andreformento.money.user.UserRegister
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Component

typealias CurrentUser = User
typealias CurrentOrganizationShare = OrganizationShare

@Component
class UserPermissionLoader(
    private val userFacade: UserFacade,
    private val organizationShares: OrganizationShares,
) {

    suspend fun getCurrentUser(principal: OAuth2User): CurrentUser? =
        // TODO metrics for: found, not found and created
        when (val email = principal.attributes["email"]) {
            is String -> {
                userFacade
                    .findByEmail(email = email)
                    ?: userFacade.create(UserRegister(name = principal.attributes["name"].toString(), email = email))
            }
            else -> null
        }

    suspend fun getCurrentOrganizationShare(
        principal: OAuth2User,
        organizationId: OrganizationId
    ): CurrentOrganizationShare? =
        when (val email = principal.attributes["email"]) {
            is String -> organizationShares.getUnsafeUserOrganization(
                userEmail = email,
                organizationId = organizationId
            )
            else -> null
        }

}
