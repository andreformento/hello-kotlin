package com.andreformento.money.organization.share.api

import com.andreformento.money.extensions.extractBody
import com.andreformento.money.organization.Organization
import com.andreformento.money.organization.api.CreatedOrganizationForTest
import com.andreformento.money.organization.api.createOrganization
import com.andreformento.money.organization.share.OrganizationShared
import com.andreformento.money.organization.share.Role
import com.andreformento.money.user.api.LoggedUserResponse
import com.andreformento.money.user.api.getUser
import com.andreformento.money.user.api.withUser
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@SpringBootTest
@AutoConfigureWebTestClient
class OrganizationShareControllerTest {

    @Autowired
    private lateinit var webClient: WebTestClient

    private lateinit var otherUser: LoggedUserResponse
    private lateinit var organizationFromOtherUser: CreatedOrganizationForTest

    private lateinit var myUser: LoggedUserResponse

    private lateinit var sharesLocation: String
    private lateinit var myShareLocation: String
    private lateinit var shareLocationFromOtherUser: String


    @BeforeEach
    fun init() {
        this.otherUser = webClient.getUser()
        this.organizationFromOtherUser = webClient.createOrganization(otherUser.email)

        this.myUser = webClient.getUser()
        this.sharesLocation = "${organizationFromOtherUser.location}/shares"

        this.myShareLocation = "${sharesLocation}/${organizationFromOtherUser.createdOrganization.organizationRole.id}"
        this.shareLocationFromOtherUser =
            "${sharesLocation}/${organizationFromOtherUser.createdOrganization.organizationRole.id}"

        assert(sharesLocation == "/organizations/${organizationFromOtherUser.createdOrganization.organization.id}/shares")
    }

    @Test
    fun `should share an organization with other user`() {
        // without access
        webClient
            .withUser(email = myUser.email)
            .get()
            .uri(organizationFromOtherUser.location)
            .exchange()
            .expectStatus().isNotFound
            .expectBody().isEmpty

        webClient
            .withUser(email = otherUser.email)
            .post()
            .uri(sharesLocation)
            .bodyValue(OrganizationRoleCreationRequest(myUser.id))
            .exchange()
            .expectStatus().isCreated
//            .expectHeaderLocation<OrganizationShared> { "${sharesLocation}/${it.id}" }
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("id").isNotEmpty
            .jsonPath("$.organizationId")
            .isEqualTo(organizationFromOtherUser.createdOrganization.organization.id.toString())
            .jsonPath("$.userId").isEqualTo(myUser.id.toString())
            .jsonPath("$.role").isEqualTo(Role.ADMIN.toString())

        // now have access
        webClient
            .withUser(email = myUser.email)
            .get()
            .uri(organizationFromOtherUser.location)
            .exchange()
            .expectStatus().isOk
            .expectBody<Organization>()
            .isEqualTo(organizationFromOtherUser.createdOrganization.organization)
    }

    @Test
    fun `should not share an organization if is not owner`() {
        webClient
            .withUser(email = myUser.email)
            .post()
            .uri(sharesLocation)
            .bodyValue(OrganizationRoleCreationRequest(myUser.id))
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    fun `should not remove share an organization if is not shared with user`() {
        webClient
            .withUser(email = myUser.email)
            .delete()
            .uri(shareLocationFromOtherUser)
            .exchange()
            .expectStatus().isForbidden
            .expectBody().isEmpty
    }

    @Test
    fun `should not remove share an organization if is not owner`() {
        // share
        webClient
            .withUser(email = otherUser.email)
            .post()
            .uri(sharesLocation)
            .bodyValue(OrganizationRoleCreationRequest(myUser.id))
            .exchange()
            .expectStatus().isCreated
            .extractBody<OrganizationShared>()

        // don't allow remove other user
        webClient
            .withUser(email = myUser.email)
            .delete()
            .uri("/organizations/${organizationFromOtherUser.createdOrganization.organization.id}/shares/${organizationFromOtherUser.createdOrganization.organizationRole.id}")
            .exchange()
            .expectStatus().isUnauthorized
            .expectBody()
            .jsonPath("$.errorMessage", "User don't have permission to do this operation")
    }

    @Test
    fun `should remove self shared an organization when is not owner`() {
        // share
        val organizationShared = webClient
            .withUser(email = otherUser.email)
            .post()
            .uri(sharesLocation)
            .bodyValue(OrganizationRoleCreationRequest(myUser.id))
            .exchange()
            .expectStatus().isCreated
            .extractBody<OrganizationShared>()

        // allow remove own user when is not owner
        webClient
            .withUser(email = myUser.email)
            .delete()
            .uri("/organizations/${organizationFromOtherUser.createdOrganization.organization.id}/shares/${organizationShared.id}")
            .exchange()
            .expectStatus().isNoContent
            .expectBody().isEmpty
    }

    @Test
    fun `should not remove self shared an organization when is owner`() {
        webClient
            .withUser(email = otherUser.email)
            .delete()
            .uri("/organizations/${organizationFromOtherUser.createdOrganization.organization.id}/shares/${organizationFromOtherUser.createdOrganization.organizationRole.id}")
            .exchange()
            .expectStatus().isUnauthorized
            .expectBody()
            .jsonPath("$.errorMessage", "Owner can't remove shared organization of himself")
    }
}
