package com.andreformento.money.organization.share

import com.andreformento.money.organization.Organization
import com.andreformento.money.organization.OrganizationId
import com.andreformento.money.user.User
import com.andreformento.money.user.UserId

import java.util.*

typealias OrganizationShareId = UUID

fun String.toOrganizationShareId(): OrganizationShareId {
    return OrganizationShareId.fromString(this)
}

enum class Role {
    OWNER,
    ADMIN,
}

sealed interface OrganizationShareDeleteResult
object OrganizationShareDeleted : OrganizationShareDeleteResult
object OrganizationShareNotFoundForThisUser : OrganizationShareDeleteResult
object OrganizationShareNotFound : OrganizationShareDeleteResult
object UserDonTHavePermissionToRemove : OrganizationShareDeleteResult
object OwnerCannotRemoveYourOwnOrganization : OrganizationShareDeleteResult

data class OrganizationShare(
    val id: OrganizationShareId,
    val organization: Organization,
    val user: User,
    val role: Role
) {

    fun canBeShared() = Role.OWNER == this.role

    fun denyReasonForNotDelete(organizationShareWhoWouldLikeRemove: OrganizationShare): OrganizationShareDeleteResult? {
        return if (this.organization != organizationShareWhoWouldLikeRemove.organization) {
            UserDonTHavePermissionToRemove
        } else {
            when (organizationShareWhoWouldLikeRemove.role) {
                Role.OWNER -> if (user == organizationShareWhoWouldLikeRemove.user) OwnerCannotRemoveYourOwnOrganization else null
                else -> if (user == organizationShareWhoWouldLikeRemove.user) null else UserDonTHavePermissionToRemove
            }
        }
    }
}

class OrganizationShareRegister(
    val organizationId: OrganizationId,
    val userId: UserId,
    val role: Role
)

class OrganizationShared(
    val id: OrganizationShareId,
    val organizationId: OrganizationId,
    val userId: UserId,
    val role: Role,
)
