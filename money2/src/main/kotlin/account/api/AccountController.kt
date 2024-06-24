package com.andreformento.money.account.api

import com.andreformento.money.account.Account
import com.andreformento.money.account.AccountFacade
import com.andreformento.money.account.AccountRegister
import com.andreformento.money.account.toAccountId
import com.andreformento.money.organization.toOrganizationId
import com.andreformento.money.user.security.UserPermissionLoader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.*
import java.net.URI

data class AccountResponse(
    val id: String,
    val name: String,
    val organizationId: String,
    val activated: Boolean,
) {
    constructor(account: Account) : this(
        id = account.id.toString(),
        name = account.name,
        organizationId = account.organizationId.toString(),
        activated = account.activated,
    )
}

@RestController
@RequestMapping("/organizations/{organization-id}/accounts", produces = ["application/json"])
class AccountController(
    private val userPermissionLoader: UserPermissionLoader,
    private val accountFacade: AccountFacade,
) {

    @PostMapping
    suspend fun create(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable("organization-id") organizationParam: String,
        @RequestBody accountRegister: AccountRegister,
    ): ResponseEntity<AccountResponse> {
        val organizationId = organizationParam.toOrganizationId()
        val currentOrganizationShare = userPermissionLoader.getCurrentOrganizationShare(principal, organizationId)
        return if (currentOrganizationShare == null) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        } else {
            accountFacade
                .create(
                    organization = currentOrganizationShare.organization,
                    accountRegister = accountRegister,
                )
                .let { AccountResponse(it) }
                .let {
                    ResponseEntity
                        .created(URI.create("/organizations/${organizationId}/accounts/${it.id}"))
                        .body(it)
                }
        }
    }

    @GetMapping("/{account-id}")
    suspend fun get(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable("organization-id") organizationParam: String,
        @PathVariable("account-id") accountParam: String,
    ): ResponseEntity<AccountResponse> {
        val organizationId = organizationParam.toOrganizationId()
        val currentOrganizationShare = userPermissionLoader.getCurrentOrganizationShare(principal, organizationId)
        return if (currentOrganizationShare == null) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        } else {
            accountFacade
                .getById(
                    organization = currentOrganizationShare.organization,
                    accountId = accountParam.toAccountId(),
                )
                ?.let { AccountResponse(it) }
                ?.let { ResponseEntity.ok(it) }
                ?: ResponseEntity.notFound().build()
        }
    }

    @GetMapping
    suspend fun getAccountsFromOrganization(
        @AuthenticationPrincipal principal: OAuth2User,
        @PathVariable("organization-id") organizationParam: String,
    ): ResponseEntity<Flow<AccountResponse>> {
        val organizationId = organizationParam.toOrganizationId()
        val currentOrganizationShare = userPermissionLoader.getCurrentOrganizationShare(principal, organizationId)
        return if (currentOrganizationShare == null) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        } else {
            accountFacade
                .getAccountsFromOrganization(organization = currentOrganizationShare.organization)
                .let { it.map { a -> AccountResponse(a) } }
                .let { ResponseEntity.ok(it) }
        }
    }

}
