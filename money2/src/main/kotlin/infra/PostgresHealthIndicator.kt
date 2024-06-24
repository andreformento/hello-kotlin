package com.andreformento.money.infra

import com.andreformento.money.user.User
import com.andreformento.money.user.repository.Users
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component

@Component
class PostgresHealthIndicator(private val repository: Users) : HealthIndicator {
    // TODO this is the best way? I hope not!
    // https://www.baeldung.com/spring-boot-health-indicators
    // https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/actuate/health/ReactiveHealthIndicator.html
    override fun health(): Health {
        return try {
            var firstEntity: User?
            runBlocking(Dispatchers.Default) {
                firstEntity = repository.findByEmail("iron-maiden@evil.hell")
            }

            if (firstEntity == null) {
                Health.down().withDetail("database", object {
                    val message = "Cannot get any data from database"
                }).build()
            } else {
                println("firstPost $firstEntity")
                Health.up().build()
            }
        } catch (e: Exception) {
            Health.down().withDetail("Cannot connect to database", e).build()
        }
    }
}
