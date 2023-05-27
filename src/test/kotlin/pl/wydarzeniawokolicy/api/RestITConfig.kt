package pl.wydarzeniawokolicy.api

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
        val restTemplateBuilder = RestTemplateBuilder()
            .rootUri(ROOT_URI)
        val testRestTemplate = TestRestTemplate(restTemplateBuilder)
        testRestTemplate.restTemplate.errorHandler = DefaultResponseErrorHandler()
        return testRestTemplate
    }
}