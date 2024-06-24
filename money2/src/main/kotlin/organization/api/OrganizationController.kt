package com.andreformento.money.organization.api

import com.andreformento.money.organization.*
import com.andreformento.money.user.security.UserPermissionLoader
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/organizations", produces = ["application/json"])
class OrganizationController(
    private val organizationFacade: OrganizationFacade,
    private val userPermissionLoader: UserPermissionLoader,
) {

    @GetMapping
    suspend fun getAllFromUser(@AuthenticationPrincipal principal: OAuth2User): ResponseEntity<Flow<Organization>> =
        userPermissionLoader
            .getCurrentUser(principal)
            ?.let { organizationFacade.getAllFromUser(it) }
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

    @PostMapping
    suspend fun create(
        @AuthenticationPrincipal principal: OAuth2User,
        @RequestBody organizationRegister: OrganizationRegister
    ): ResponseEntity<CreatedOrganization> =
        userPermissionLoader
            .getCurrentUser(principal)
            ?.let {
                val createdOrganization = organizationFacade.create(it, organizationRegister)
                ResponseEntity
                    .created(URI.create("/organizations/${createdOrganization.organization.id}"))
                    .body(createdOrganization)
            }
            ?: ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

    @GetMapping("/{organization-id}")
    suspend fun getById(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable("organization-id") organizationParam: String
    ): ResponseEntity<Organization> =
        userPermissionLoader
            .getCurrentOrganizationShare(
                principal = principal,
                organizationId = organizationParam.toOrganizationId()
            )
            ?.let { ResponseEntity.ok(it.organization) }
            ?: ResponseEntity.notFound().build()

    @PutMapping("/{organization-id}")
    suspend fun update(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable("organization-id") organizationParam: String,
        @RequestBody organizationRegister: OrganizationRegister
    ): ResponseEntity<Organization> =
        userPermissionLoader
            .getCurrentOrganizationShare(
                principal = principal,
                organizationId = organizationParam.toOrganizationId()
            )
            ?.let {
                organizationFacade.update(
                    organizationId = it.organization.id,
                    organizationRegister = organizationRegister,
                )
            }
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    // TODO
//    @DeleteMapping("/{organization-id}")
//    suspend fun delete(
//        @AuthenticationPrincipal principal: OAuth2User,
//        @PathVariable("organization-id") organizationId: String
//    ): ResponseEntity<Any> {
//        val deletedCount = organizationFacade.delete(organizationId.toOrganizationId())
//        println("$deletedCount organizations deleted")
//        return ResponseEntity.noContent().build()
//    }

}
