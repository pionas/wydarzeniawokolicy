package pl.wydarzeniawokolicy.api.users

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.whenever
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpClientErrorException
import pl.wydarzeniawokolicy.api.BasicIT
import java.time.LocalDateTime
import java.util.stream.Stream


internal class UserRestControllerIT : BasicIT() {

    @Test
    @Disabled
    fun shouldReturnUnauthorized() {
        // given

        // when
        val result = restApiTemplate.getForEntity("/users", List::class.java)

        // then
        assertNotNull(result)
        assertEquals(HttpStatus.UNAUTHORIZED, result?.statusCode)
    }

    @Test
    @Sql("/db/users.sql")
    fun shouldReturnUserList() {
        // given
        // when
        val result: ResponseEntity<List<UserDto>> = restApiTemplate.exchange(
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
                { restApiTemplate.postForEntity("/users", HashMap<String, Any>(), Any::class.java) },
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
                { restApiTemplate.postForEntity("/users", signUpDto, Any::class.java) },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.BAD_REQUEST, result?.statusCode)
    }

    @Test
    @Sql("/db/users.sql")
    fun shouldCreate() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 22, 11, 12, 0, 0)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        val signUpDto = SignUpDto("name", "email@example.com", "password", "password")
        // when
        val user = restApiTemplate.postForEntity("/users", signUpDto, UserDto::class.java)
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
        val result = restApiTemplate.getForEntity("/users/1", UserDto::class.java)
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
                { restApiTemplate.getForEntity("/users/100", Object::class.java) },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.NOT_FOUND, result?.statusCode)
    }

    @Test
    fun shouldReturnInternalServerErrorWhenTryUpdate() {
        // given
        // when
        val result =
            Assertions.catchThrowableOfType(
                { restApiTemplate.put("/users/1", HashMap<String, Any>(), Any::class.java) },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.BAD_REQUEST, result?.statusCode)
    }

    @Test
    fun shouldReturnNotFoundWhenTryUpdateByCategoryNotExist() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 22, 11, 12, 0, 0)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        val userDetailsDto = UserDetailsDto("User", "user@example.com", "oldPassword", null, null)
        // when
        val requestEntity = HttpEntity(userDetailsDto)
        val result =
            Assertions.catchThrowableOfType(
                {
                    restApiTemplate.exchange(
                        "/users/1",
                        HttpMethod.PUT,
                        requestEntity,
                        UserDto::class.java
                    )
                },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.NOT_FOUND, result?.statusCode)
    }

    @Test
    @Sql("/db/users.sql")
    fun shouldReturnBadRequestWhenTryUpdateButUserByEmailExist() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 22, 11, 12, 0, 0)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        val userDetailsDto = UserDetailsDto("User", "anna.doe@example.com", "oldPassword", null, null)
        // when
        val requestEntity = HttpEntity(userDetailsDto)
        val result =
            Assertions.catchThrowableOfType(
                {
                    restApiTemplate.exchange(
                        "/users/1",
                        HttpMethod.PUT,
                        requestEntity,
                        UserDto::class.java
                    )
                },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.BAD_REQUEST, result?.statusCode)
    }

    @Test
    @Sql("/db/users.sql")
    fun shouldReturnBadRequestWhenTryUpdateButUserByNameExist() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 22, 11, 12, 0, 0)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        val userDetailsDto = UserDetailsDto("anna.doe", "john.doe@example.com", "oldPassword", null, null)
        // when
        val requestEntity = HttpEntity(userDetailsDto)
        val result =
            Assertions.catchThrowableOfType(
                {
                    restApiTemplate.exchange(
                        "/users/1",
                        HttpMethod.PUT,
                        requestEntity,
                        UserDto::class.java
                    )
                },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.BAD_REQUEST, result?.statusCode)
    }

    @ParameterizedTest
    @MethodSource("provideUserDtoList")
    @Sql("/db/users.sql")
    fun shouldReturnBadRequestWhenTryUpdate(
        name: String,
        email: String,
        oldPassword: String,
        newPassword: String?,
        confirmNewPassword: String?,
        message: String
    ) {
        // given
        val userDetailsDto = UserDetailsDto(
            name = name,
            email = email,
            oldPassword = oldPassword,
            password = newPassword,
            passwordConfirm = confirmNewPassword
        )
        // when
        val result =
            Assertions.catchThrowableOfType(
                { restApiTemplate.put("/users/1", userDetailsDto, Any::class.java) },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.BAD_REQUEST, result!!.statusCode)
        assertTrue(result.message!!.contains(message))
    }

    @ParameterizedTest
    @MethodSource("provideUserDtoValidList")
    @Sql("/db/users.sql")
    fun shouldUpdate(
        name: String,
        email: String,
        oldPassword: String,
        newPassword: String?,
        confirmNewPassword: String?
    ) {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 25, 19, 57, 0, 0)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        val userDetailsDto = UserDetailsDto(
            name = name,
            email = email,
            oldPassword = oldPassword,
            password = newPassword,
            passwordConfirm = confirmNewPassword
        )
        // when
        val requestEntity = HttpEntity(userDetailsDto)
        val category: ResponseEntity<UserDto> =
            restApiTemplate.exchange("/users/1", HttpMethod.PUT, requestEntity, UserDto::class.java)
        // then
        assertNotNull(category)
        assertEquals(HttpStatus.OK, category.statusCode)
        Assertions.assertThat(category.body)
            .hasFieldOrPropertyWithValue("name", name)
            .hasFieldOrPropertyWithValue("email", email)
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 23, 13, 7, 0, 0))
            .hasFieldOrPropertyWithValue("updatedAt", localDateTime)
    }

    @Test
    @Sql("/db/users.sql")
    fun shouldDelete() {
        // given
        // when
        val result = restApiTemplate.exchange("/users/1", HttpMethod.DELETE, null, Any::class.java)
        // then
        assertEquals(HttpStatus.OK, result.statusCode)
    }

    companion object {
        @JvmStatic
        private fun provideUserDtoList(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("", "", "", null, null, "parameter name"),
                Arguments.of("name1", "", "", null, null, "parameter email"),
                Arguments.of("name1", "valid@email.com", "", null, null, "parameter oldPassword"),
                Arguments.of(
                    "name1",
                    "email@example.com",
                    "validPassword",
                    null,
                    "z",
                    "userDetailsDto.passwordConfirm"
                ),
                Arguments.of("name1", "email@example.com", "validPassword", "x", "z", "userDetailsDto.password"),
                Arguments.of(
                    "name1",
                    "email@example.com",
                    "validPassword",
                    "xdsadsa",
                    "z",
                    "userDetailsDto.passwordConfirm"
                ),
                Arguments.of(
                    "name1",
                    "email@example.com",
                    "validPassword",
                    "xdsadsa",
                    "dsadadaz",
                    "PasswordConfirmAnnotation"
                ),
                Arguments.of(
                    "VeryLongNameVeryLongNameVeryLongNameVeryLongNameVeryLongName",
                    "email@example.com",
                    "validPassword",
                    "xdsadsa",
                    "dsadadaz",
                    "userDetailsDto.name"
                ),
            )
        }

        @JvmStatic
        private fun provideUserDtoValidList(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("name1", "email@example.com", "validPassword", null, null),
                Arguments.of("name1", "email@example.com", "validPassword", "newPassword", "newPassword"),
            )
        }
    }

}