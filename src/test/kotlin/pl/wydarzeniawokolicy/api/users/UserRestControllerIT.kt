package pl.wydarzeniawokolicy.api.users

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpRequest
import org.springframework.http.client.ClientHttpResponse
import pl.wydarzeniawokolicy.api.BasicIT


internal class UserRestControllerIT : BasicIT() {

    @Test
    fun shouldReturnUnauthorized() {
        // given

        // when
        val result = restTemplate.getForEntity("/api/v1/users", List::class.java)

        // then
        assertNotNull(result)
        assertEquals(HttpStatus.UNAUTHORIZED, result?.statusCode)
    }

    @Test
    fun shouldReturnUserList() {
        // given

        // when
        val result = restTemplate.getForEntity("/api/v1/users", List::class.java)

        // then
        assertNotNull(result)
        assertEquals(HttpStatus.OK, result?.statusCode)
    }

    @Test
    fun shouldReturnBadRequestWhenTryCreate() {
        // given
        // when
        val result = restTemplate.postForEntity("/api/v1/users", null, UserDto::class.java)
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.CREATED, result?.statusCode)
    }

    @Test
    fun shouldCreate() {
        // given
        val signUpDto = SignUpDto("name", "email", "password", "password")
        // when
        val result = restTemplate.postForEntity("/api/v1/users", signUpDto, UserDto::class.java)
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.CREATED, result?.statusCode)
    }

    @Test
    fun shouldReturnUserDetailsById() {
        // given
        // when
        val result = restTemplate.getForEntity("/api/v1/users/1", UserDto::class.java)
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.OK, result?.statusCode)
    }

    @Test
    fun shouldReturnNotFoundWhenTryGetUserDetailsById() {
        // given
        // when
        val result = restTemplate.getForEntity("/api/v1/users/100", UserDto::class.java)
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.NOT_FOUND, result?.statusCode)
    }

    @Test
    fun shouldUpdateUserDetails() {
    }

    @Test
    fun shouldReturnNotFoundWhenTryUpdateUserDetailsButUserNotExist() {
    }

    @Test
    fun shouldReturnNotFoundWhenTryUpdateUserDetails() {
        // null, empty invalid name, email, password, passwordConfirm
    }

    @Test
    fun shouldDelete() {
        // given
        // when
        val result =
            restTemplate.execute(
                "/api/v1/users/100",
                HttpMethod.DELETE,
                { _: ClientHttpRequest -> },
                { _: ClientHttpResponse -> null })
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.OK, result)
    }
}