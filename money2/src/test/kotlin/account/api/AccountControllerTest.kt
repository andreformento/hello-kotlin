package com.andreformento.money.account.api

import com.andreformento.money.account.AccountRegister
import com.andreformento.money.organization.api.CreatedOrganizationForTest
import com.andreformento.money.organization.api.createOrganization
import com.andreformento.money.user.api.withUser
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList
import org.springframework.test.web.reactive.server.returnResult
import java.net.URI
import java.time.Duration

class AccountResponseForTest(val response: AccountResponse, val org: CreatedOrganizationForTest, val location: URI)

fun WebTestClient.createAccount(org: CreatedOrganizationForTest): AccountResponseForTest {
    // create
    val response = this
        .withUser(org.userEmail)
        .post()
        .uri("${org.uri()}/accounts")
        .bodyValue(AccountRegister(name = "New account"))
        .exchange()
        .expectStatus().isCreated
        .returnResult<AccountResponse>()

    val createdEntity = response.responseBody.blockFirst()!!

    assertThat(createdEntity.id).isNotNull
    assertThat(createdEntity.name).isEqualTo("New account")
    assertThat(createdEntity.organizationId).isEqualTo(org.createdOrganization.organization.id.toString())
    assertThat(createdEntity.activated).isTrue

    return AccountResponseForTest(createdEntity, org, response.responseHeaders.location!!)
}


@SpringBootTest
@AutoConfigureWebTestClient
class AccountControllerTest {

    @Autowired
    private lateinit var webClient: WebTestClient

    private lateinit var organization: CreatedOrganizationForTest
    private lateinit var account: AccountResponseForTest

    @BeforeEach
    fun init() {
        this.webClient = webClient.mutate()
            .responseTimeout(Duration.ofSeconds(60))
            .build()

        this.organization = webClient.createOrganization()
        this.account = webClient.createAccount(organization)
    }

    @Test
    fun `should create an account`() {
        assertThat(this.account).isNotNull
    }

    @Test
    fun `should get an account from user`() {
        webClient
            .withUser(account.org.userEmail)
            .get()
            .uri(account.location)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("id").isNotEmpty
            .jsonPath("name").isEqualTo("New account")
            .jsonPath("organizationId").isEqualTo(account.org.createdOrganization.organization.id.toString())
            .jsonPath("activated").isEqualTo(true)
    }

    @Test
    fun `should not get an account in a organization not shared with the user`() {
        val otherOrg = webClient.createOrganization()
        val otherAccount = webClient.createAccount(otherOrg)

        webClient
            .withUser(account.org.userEmail)
            .get()
            .uri(otherAccount.location)
            .exchange()
            .expectStatus().isForbidden
            .expectBody().isEmpty
    }

    @Test
    fun `should get all my accounts in organization`() {
        val anotherAccount = webClient.createAccount(organization)

        webClient
            .withUser(account.org.userEmail)
            .get()
            .uri("${account.org.uri()}/accounts")
            .exchange()
            .expectStatus().isOk
            .expectBodyList<AccountResponse>()
            .hasSize(2)
            .contains(
                account.response,
                anotherAccount.response,
            )
    }

}
