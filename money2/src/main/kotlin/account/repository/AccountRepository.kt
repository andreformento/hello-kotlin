package com.andreformento.money.account.repository

import com.andreformento.money.account.AccountId
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
internal interface AccountRepository : CoroutineCrudRepository<AccountEntity, AccountId> {

    suspend fun findByIdAndOrganizationId(id: String, organizationId: String): AccountEntity?

    suspend fun findByOrganizationId(organizationId: String): Flow<AccountEntity>

}
