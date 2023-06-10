package pl.wydarzeniawokolicy.api

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.web.client.DefaultResponseErrorHandler


@TestConfiguration
class RestITConfig {

    private val ROOT_URI = "http://localhost:7778/"
    private val ROOT_API_URI = ROOT_URI.plus("api/v1")

    @Bean
    fun restApiTemplate(): TestRestTemplate {
        return buildRestTemplate(null, null, ROOT_API_URI)
    }

    @Bean
    fun authorizedRestTemplate(
        @Value("\${users.valid.username}") username: String,
        @Value("\${users.valid.password}") password: String
    ): TestRestTemplate {
        return buildRestTemplate(username, password, ROOT_API_URI)
    }

    @Bean
    fun forbiddenRestTemplate(
        @Value("\${users.invalid.username}") username: String,
        @Value("\${users.invalid.password}") password: String
    ): TestRestTemplate {
        return buildRestTemplate(username, password, ROOT_API_URI)
    }

    @Bean
    fun wrongPasswordRestTemplate(@Value("\${users.valid.username}") username: String): TestRestTemplate {
        return buildRestTemplate(username, username, ROOT_API_URI)
    }

    private fun buildRestTemplate(
        username: String?,
        password: String?,
        rootUri: String
    ): TestRestTemplate {
        val restTemplateBuilder = RestTemplateBuilder()
            .rootUri(rootUri)
        val testRestTemplate = TestRestTemplate(restTemplateBuilder, username, password)
        testRestTemplate.restTemplate.errorHandler = DefaultResponseErrorHandler()
        return testRestTemplate
    }
}