package com.andreformento.money.user.api

import com.andreformento.money.extensions.extractBody
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOAuth2Login
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.random.Random

fun createAnEmail() = "eddie${Random.nextInt()}@test.com"

fun WebTestClient.withUser(email: String = createAnEmail()): WebTestClient = this
    .mutateWith(mockOAuth2Login()
        .attributes { attrs ->
            attrs["email"] = email
            attrs["name"] = "Eddie"
        }
    )

fun WebTestClient.getUser(email: String = createAnEmail()): LoggedUserResponse = this
    .withUser(email)
    .get()
    .uri("/user")
    .exchange()
    .expectStatus().isOk
    .extractBody()

@SpringBootTest
@AutoConfigureWebTestClient
class UserControllerTest {

    @Autowired
    private lateinit var webClient: WebTestClient

    @Test
    fun `can obtain own user details when logged on`() {
        val givenEmail = createAnEmail()
        val (id, name, email) = webClient.getUser(givenEmail)

        assertThat(id).isNotNull
        assertThat(name).isEqualTo("Eddie")
        assertThat(email).isEqualTo(email)
    }

    @Test
    fun `can not obtain own user details when logged off`() {
        webClient
            .get()
            .uri("/user")
            .exchange()
            .expectStatus().isFound
            .expectHeader().location("/oauth2/authorization/google")
    }

}
