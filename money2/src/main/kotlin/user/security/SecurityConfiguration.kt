package com.andreformento.money.user.security

import com.andreformento.money.organization.share.repository.OrganizationShares
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
class SecurityConfiguration {

    val permittedGetPaths = arrayOf(
        // actuator
        "/actuator/health",
        "/actuator/health/liveness",
        "/actuator/health/readiness",
        "/actuator/prometheus",
    )

    @Bean
    fun securityWebFilterChain(
        http: ServerHttpSecurity,
        organizationShares: OrganizationShares,
    ): SecurityWebFilterChain {
        return http
            .authorizeExchange()
            .pathMatchers(HttpMethod.GET, *permittedGetPaths).permitAll()
            .anyExchange().authenticated()
            .and()
            .oauth2Login()
            .and()
            .httpBasic().disable()
            .csrf().disable()
//            .formLogin().disable()
//            .logout().disable()
            .build()
    }
}

