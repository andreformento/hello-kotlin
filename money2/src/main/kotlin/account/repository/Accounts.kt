package com.andreformento.money.account.repository

import com.andreformento.money.account.Account
import com.andreformento.money.account.AccountId
import com.andreformento.money.account.AccountRegister
import com.andreformento.money.organization.OrganizationId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Repository

@Repository
class Accounts internal constructor(private val accountRepository: AccountRepository) {

    suspend fun findById(organizationId: OrganizationId, accountId: AccountId): Account? =
        accountRepository.findByIdAndOrganizationId(
            organizationId = organizationId.toString(),
            id = accountId.toString(),
        )?.toModel()

    suspend fun save(organizationId: OrganizationId, accountRegister: AccountRegister): Account =
        accountRepository.save(AccountEntity(organizationId, accountRegister)).toModel()

    suspend fun findByOrganization(organizationId: OrganizationId): Flow<Account> =
        accountRepository.findByOrganizationId(organizationId = organizationId.toString()).map(AccountEntity::toModel)

}
