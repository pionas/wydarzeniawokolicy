package pl.wydarzeniawokolicy.api

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.web.client.DefaultResponseErrorHandler


@TestConfiguration
class RestITConfig {

    private val ROOT_URI = "http://localhost:7778/api/v1"

    @Bean
    fun testRestTemplate(): TestRestTemplate {
        return buildRestTemplate(null, null)
    }

    @Bean
    fun authorizedRestTemplate(
        @Value("\${users.valid.username}") username: String,
        @Value("\${users.valid.password}") password: String
    ): TestRestTemplate {
        return buildRestTemplate(username, password)
    }

    @Bean
    fun forbiddenRestTemplate(
        @Value("\${users.invalid.username}") username: String,
        @Value("\${users.invalid.password}") password: String
    ): TestRestTemplate {
        return buildRestTemplate(username, password)
    }

    @Bean
    fun wrongPasswordRestTemplate(@Value("\${users.valid.username}") username: String): TestRestTemplate {
        return buildRestTemplate(username, username)
    }

    private fun buildRestTemplate(
        username: String?,
        password: String?
    ): TestRestTemplate {
        val restTemplateBuilder = RestTemplateBuilder()
            .rootUri(ROOT_URI)
        val testRestTemplate = TestRestTemplate(restTemplateBuilder)
        testRestTemplate.restTemplate.errorHandler = DefaultResponseErrorHandler()
        if (listOfNotNull(username, password).size == 2) {
            testRestTemplate.withBasicAuth(username, password)
        }
        return testRestTemplate
    }
}