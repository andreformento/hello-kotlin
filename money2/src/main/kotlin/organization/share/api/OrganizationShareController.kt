package com.andreformento.money.organization.share.api

import com.andreformento.money.organization.share.*
import com.andreformento.money.organization.toOrganizationId
import com.andreformento.money.user.UserId
import com.andreformento.money.user.security.UserPermissionLoader
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.*
import java.net.URI

data class OrganizationRoleCreationRequest(
    val userId: UserId,
)

@RestController
@RequestMapping("/organizations/{organization-id}/shares", produces = ["application/json"])
class OrganizationShareController(
    private val organizationShareFacade: OrganizationShareFacade,
    private val userPermissionLoader: UserPermissionLoader,
) {

    @PostMapping
    suspend fun shareOrganization(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable("organization-id") organizationParam: String,
        @RequestBody organizationRoleCreationRequest: OrganizationRoleCreationRequest,
    ): ResponseEntity<OrganizationShared> {
        val organizationId = organizationParam.toOrganizationId()
        return if (userPermissionLoader.getCurrentOrganizationShare(principal, organizationId)?.canBeShared() == true) {
            organizationShareFacade
                .share(
                    userId = organizationRoleCreationRequest.userId,
                    organizationId = organizationId,
                )
                .let {
                    ResponseEntity
                        .created(URI.create("/organizations/${organizationId}/shares/${it.id}"))
                        .body(it)
                }
        } else {
            ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
    }

    @DeleteMapping("/{organization-share-id}")
    suspend fun delete(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable("organization-id") organizationParam: String,
        @PathVariable("organization-share-id") organizationShareParam: String,
    ): ResponseEntity<Any> {
        val organizationId = organizationParam.toOrganizationId()
        val authenticatedOrganizationShare = userPermissionLoader.getCurrentOrganizationShare(principal, organizationId)

        return if (authenticatedOrganizationShare == null) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        } else {
            organizationShareFacade
                .delete(
                    organizationId = organizationParam.toOrganizationId(),
                    organizationShareIdToBeRemoved = organizationShareParam.toOrganizationShareId(),
                    authenticatedOrganizationShare = authenticatedOrganizationShare
                ).let {
                    when (it) {
                        is OrganizationShareDeleted -> ResponseEntity.noContent().build()
                        is OrganizationShareNotFoundForThisUser -> ResponseEntity.notFound().build()
                        is OrganizationShareNotFound -> ResponseEntity.notFound().build()
                        is UserDonTHavePermissionToRemove -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(object {
                                val errorMessage = "User don't have permission to do this operation"
                            })
                        is OwnerCannotRemoveYourOwnOrganization -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(object {
                                val errorMessage = "Owner can't remove shared organization of himself"
                            })
                    }
                }
        }
    }
}
