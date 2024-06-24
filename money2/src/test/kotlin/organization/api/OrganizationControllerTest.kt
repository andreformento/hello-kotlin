package com.andreformento.money.organization.api

import com.andreformento.money.organization.CreatedOrganization
import com.andreformento.money.organization.Organization
import com.andreformento.money.organization.OrganizationRegister
import com.andreformento.money.user.api.createAnEmail
import com.andreformento.money.user.api.withUser
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList
import org.springframework.test.web.reactive.server.returnResult
import java.net.URI

class CreatedOrganizationForTest(val userEmail: String, val createdOrganization: CreatedOrganization, val location: URI) {
    fun uri() = "/organizations/${this.createdOrganization.organization.id}"
}

fun WebTestClient.createOrganization(email: String = createAnEmail()): CreatedOrganizationForTest {
    // create
    val response = this
        .withUser(email)
        .post()
        .uri("/organizations")
        .bodyValue(OrganizationRegister("my-new-org", "some description"))
        .exchange()
        .expectStatus().isCreated
        .returnResult<CreatedOrganization>()

    val createdOrganization = response.responseBody.blockFirst()!!

    assertThat(createdOrganization.organization.id).isNotNull
    assertThat(createdOrganization.organization.name).isEqualTo("my-new-org")
    assertThat(createdOrganization.organization.description).isEqualTo("some description")

    return CreatedOrganizationForTest(
        userEmail = email,
        createdOrganization = createdOrganization,
        location = response.responseHeaders.location!!
    )
}

@SpringBootTest
@AutoConfigureWebTestClient
class OrganizationControllerTest {

    @Autowired
    private lateinit var webClient: WebTestClient

    @Test
    fun `can obtain all own user organizations`() {
        val email = createAnEmail()
        val organization1 = webClient.createOrganization(email)
        val organization2 = webClient.createOrganization(email)

        webClient
            .withUser(email = email)
            .get()
            .uri("/organizations")
            .exchange()
            .expectStatus().isOk
            .expectBodyList<Organization>()
            .hasSize(2)
            .contains(
                organization1.createdOrganization.organization,
                organization2.createdOrganization.organization,
            )
    }

    @Test
    fun `can obtain my own user organization`() {
        val email = createAnEmail()
        val organization = webClient.createOrganization(email)

        webClient
            .withUser(email = email)
            .get()
            .uri(organization.location)
            .exchange()
            .expectStatus().isOk
            .expectBody<Organization>()
            .isEqualTo(organization.createdOrganization.organization)
    }

    @Test
    fun `can't obtain organization not shared with user`() {
        val emailFromOtherUser = createAnEmail()
        val myEmail = createAnEmail()

        val organizationFromOtherUser = webClient.createOrganization(emailFromOtherUser)

        webClient
            .withUser(email = myEmail)
            .get()
            .uri(organizationFromOtherUser.location)
            .exchange()
            .expectStatus().isNotFound
            .expectBody().isEmpty
    }

    @Test
    fun `should update an organization`() {
        val email = createAnEmail()
        val organization = webClient.createOrganization(email)

        webClient
            .withUser(email)
            .put()
            .uri(organization.location)
            .bodyValue(OrganizationRegister("my-new-org-updated", "some description-updated"))
            .exchange()
            .expectStatus().isOk
            .expectBody<Organization>()
            .isEqualTo(
                Organization(
                    id = organization.createdOrganization.organization.id,
                    name = "my-new-org-updated",
                    description = "some description-updated"
                )
            )
    }

    // TODO test delete

}
