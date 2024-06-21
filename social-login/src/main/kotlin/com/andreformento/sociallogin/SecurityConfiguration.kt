package com.andreformento.sociallogin

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
class SecurityConfiguration {

    @Bean
    fun clientRegistration(
        @Value("\${spring.security.oauth2.client.registration.google.client-id}") clientId: String,
        @Value("\${spring.security.oauth2.client.registration.google.client-secret}") clientSecret: String,
    ): ClientRegistration =
        CommonOAuth2Provider
            .GOOGLE
            .getBuilder(CommonOAuth2Provider.GOOGLE.name.lowercase())
            .clientId(clientId)
            .clientSecret(clientSecret)
            .build()

    @Bean
    fun clientRegistrationRepository(clientRegistration: ClientRegistration): InMemoryReactiveClientRegistrationRepository {
        return InMemoryReactiveClientRegistrationRepository(clientRegistration);
    }

    @Bean
    fun authorizedClientService(clientRegistrationRepository: InMemoryReactiveClientRegistrationRepository): InMemoryReactiveOAuth2AuthorizedClientService =
        InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrationRepository)

    @Bean
    fun securityWebFilterChain(
        http: ServerHttpSecurity,
        clientRegistrationRepository: InMemoryReactiveClientRegistrationRepository,
        authorizedClientService: InMemoryReactiveOAuth2AuthorizedClientService,
    ): SecurityWebFilterChain = http
        .authorizeExchange()
        .anyExchange()
        .authenticated()
        .and().oauth2Login()
        .clientRegistrationRepository(clientRegistrationRepository)
        .authorizedClientService(authorizedClientService)
        .and().build()

}
