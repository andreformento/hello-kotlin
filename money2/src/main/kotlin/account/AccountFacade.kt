package com.andreformento.money.account

import com.andreformento.money.account.repository.Accounts
import com.andreformento.money.organization.Organization
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class AccountFacade(private val accounts: Accounts) {

    suspend fun create(organization: Organization, accountRegister: AccountRegister): Account =
        accounts.save(
            organizationId = organization.id,
            accountRegister = accountRegister,
        )

    suspend fun getAccountsFromOrganization(organization: Organization): Flow<Account> =
        accounts.findByOrganization(organizationId = organization.id)

    suspend fun getById(organization: Organization, accountId: AccountId) = accounts.findById(
        organizationId = organization.id,
        accountId = accountId,
    )

}
