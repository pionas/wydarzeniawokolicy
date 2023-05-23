package pl.wydarzeniawokolicy.api

import jakarta.persistence.EntityManager
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class DbItConfig {

    @Bean
    fun dbUtils(entityManager: EntityManager): TestDbUtil {
        return TestDbUtil(entityManager)
    }
}
