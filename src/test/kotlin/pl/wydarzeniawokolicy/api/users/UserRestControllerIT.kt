package pl.wydarzeniawokolicy.api.users

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpClientErrorException
import pl.wydarzeniawokolicy.api.BasicIT
import java.time.LocalDateTime


internal class UserRestControllerIT : BasicIT() {

    @Test
    @Disabled
    fun shouldReturnUnauthorized() {
        // given

        // when
        val result = restTemplate.getForEntity("/users", List::class.java)

        // then
        assertNotNull(result)
        assertEquals(HttpStatus.UNAUTHORIZED, result?.statusCode)
    }

    @Test
    @Sql("/db/users.sql")
    fun shouldReturnUserList() {
        // given
        // when
        val result: ResponseEntity<List<UserDto>> = restTemplate.exchange(
            "/users",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<List<UserDto>>() {})
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.OK, result.statusCode)
        val users = result.body!!
        Assertions.assertThat(users).hasSize(3)
        Assertions.assertThat(users[0])
            .hasFieldOrPropertyWithValue("id", 1L)
            .hasFieldOrPropertyWithValue("name", "john.doe")
            .hasFieldOrPropertyWithValue("email", "john.doe@example.com")
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 23, 13, 7, 0, 0))
        Assertions.assertThat(users[1])
            .hasFieldOrPropertyWithValue("id", 2L)
            .hasFieldOrPropertyWithValue("name", "anna.doe")
            .hasFieldOrPropertyWithValue("email", "anna.doe@example.com")
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 23, 13, 7, 10, 0))
        Assertions.assertThat(users[2])
            .hasFieldOrPropertyWithValue("id", 3L)
            .hasFieldOrPropertyWithValue("name", "jane.doe")
            .hasFieldOrPropertyWithValue("email", "jane.doe@example.com")
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 23, 13, 7, 20, 0))
    }

    @Test
    fun shouldReturnInternalServerErrorWhenTryCreate() {
        // given
        // when
        val result =
            Assertions.catchThrowableOfType(
                { restTemplate.postForEntity("/users", HashMap<String, Any>(), Any::class.java) },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.BAD_REQUEST, result?.statusCode)
    }

    @Test
    fun shouldReturnBadRequestWhenTryCreate() {
        // given
        val signUpDto = SignUpDto("", "", "", "")
        // when
        val result =
            Assertions.catchThrowableOfType(
                { restTemplate.postForEntity("/users", signUpDto, Any::class.java) },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.BAD_REQUEST, result?.statusCode)
    }

    @Test
    fun shouldCreate() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 22, 11, 12, 0, 0)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        val signUpDto = SignUpDto("name", "email@example.com", "password", "password")
        // when
        val user = restTemplate.postForEntity("/users", signUpDto, UserDto::class.java)
        // then
        assertNotNull(user)
        assertEquals(HttpStatus.CREATED, user?.statusCode)
        Assertions.assertThat(user.body)
            .hasFieldOrPropertyWithValue("id", 1L)
            .hasFieldOrPropertyWithValue("name", signUpDto.name)
            .hasFieldOrPropertyWithValue("email", signUpDto.email)
            .hasFieldOrPropertyWithValue("createdAt", localDateTime)
    }

    @Test
    @Sql("/db/users.sql")
    fun shouldReturnUserDetailsById() {
        // given
        // when
        val result = restTemplate.getForEntity("/users/1", UserDto::class.java)
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.OK, result?.statusCode)
        Assertions.assertThat(result.body!!)
            .hasFieldOrPropertyWithValue("id", 1L)
            .hasFieldOrPropertyWithValue("name", "john.doe")
            .hasFieldOrPropertyWithValue("email", "john.doe@example.com")
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 23, 13, 7, 0, 0))
    }

    @Test
    fun shouldReturnNotFoundWhenTryGetUserDetailsById() {
        // given
        // when
        val result =
            Assertions.catchThrowableOfType(
                { restTemplate.getForEntity("/users/100", Object::class.java) },
                HttpClientErrorException::class.java
            )
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
        val result = restTemplate.exchange("/users/100", HttpMethod.DELETE, null, Any::class.java)
        // then
        assertEquals(HttpStatus.OK, result.statusCode)
    }
}