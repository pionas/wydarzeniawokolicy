package pl.wydarzeniawokolicy.api.roles

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
import pl.wydarzeniawokolicy.BasicIT
import pl.wydarzeniawokolicy.infrastructure.database.roles.RoleEntity
import java.time.LocalDateTime
import java.util.stream.Stream

internal class RoleRestControllerIT : BasicIT() {

    @Test
    @Disabled
    fun shouldReturnUnauthorized() {
        // given

        // when
        val result = restApiTemplate.getForEntity("/roles", List::class.java)

        // then
        assertNotNull(result)
        assertEquals(HttpStatus.UNAUTHORIZED, result?.statusCode)
    }

    @Test
    @Sql("/db/roles.sql")
    fun shouldReturnRoleList() {
        // given
        // when
        val result: ResponseEntity<List<RoleDto>> = restApiTemplate.exchange(
            "/roles",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<List<RoleDto>>() {})
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.OK, result.statusCode)
        val roles = result.body!!
        Assertions.assertThat(roles).hasSize(7)
        Assertions.assertThat(roles[0])
            .hasFieldOrPropertyWithValue("name", "Role 1")
            .hasFieldOrPropertyWithValue("slug", "role_1")
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 28, 7, 34, 0, 0))
        Assertions.assertThat(roles[1])
            .hasFieldOrPropertyWithValue("name", "Role 2")
            .hasFieldOrPropertyWithValue("slug", "role_2")
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 28, 7, 34, 10, 0))
        Assertions.assertThat(roles[2])
            .hasFieldOrPropertyWithValue("name", "Role 3")
            .hasFieldOrPropertyWithValue("slug", "role-3")
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 28, 7, 34, 20, 0))
    }

    @Test
    fun shouldReturnInternalServerErrorWhenTryCreate() {
        // given
        // when
        val result =
            Assertions.catchThrowableOfType(
                { restApiTemplate.postForEntity("/roles", HashMap<String, Any>(), Any::class.java) },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.BAD_REQUEST, result?.statusCode)
    }

    @Test
    fun shouldReturnBadRequestWhenTryCreate() {
        // given
        val roleDto = NewRoleDto("", null)
        // when
        val result =
            Assertions.catchThrowableOfType(
                { restApiTemplate.postForEntity("/roles", roleDto, Any::class.java) },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.BAD_REQUEST, result?.statusCode)
    }

    @Test
    @Sql("/db/roles.sql")
    fun shouldCreate() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 22, 11, 12, 0, 0)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        val roleDto = NewRoleDto("Role 1", null)
        // when
        val role = restApiTemplate.postForEntity("/roles", roleDto, RoleDto::class.java)
        // then
        assertNotNull(role)
        assertEquals(HttpStatus.CREATED, role?.statusCode)
        Assertions.assertThat(role.body)
            .hasFieldOrPropertyWithValue("name", roleDto.name!!.uppercase())
            .hasFieldOrPropertyWithValue("slug", "role_11")
            .hasFieldOrPropertyWithValue("createdAt", localDateTime)
    }

    @Test
    @Sql("/db/roles.sql")
    fun shouldReturnRoleDetailsById() {
        // given
        // when
        val result = restApiTemplate.getForEntity("/roles/role_2", RoleDto::class.java)
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.OK, result?.statusCode)
        Assertions.assertThat(result.body!!)
            .hasFieldOrPropertyWithValue("name", "Role 2")
            .hasFieldOrPropertyWithValue("slug", "role_2")
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 28, 7, 34, 10, 0))
            .hasFieldOrPropertyWithValue("updatedAt", LocalDateTime.of(2023, 5, 28, 8, 45, 10, 0))
    }

    @Test
    fun shouldReturnNotFoundWhenTryGetRoleDetailsById() {
        // given
        // when
        val result =
            Assertions.catchThrowableOfType(
                { restApiTemplate.getForEntity("/roles/100", Object::class.java) },
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
                { restApiTemplate.put("/roles/role_1", HashMap<String, Any>(), Any::class.java) },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.BAD_REQUEST, result?.statusCode)
    }

    @Test
    fun shouldReturnNotFoundWhenTryUpdateByRoleNotExist() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 22, 11, 12, 0, 0)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        val roleDto = NewRoleDto("Role 1", "role-1")
        // when
        val requestEntity = HttpEntity(roleDto)
        val result =
            Assertions.catchThrowableOfType(
                { restApiTemplate.exchange("/roles/role-1", HttpMethod.PUT, requestEntity, RoleDto::class.java) },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.NOT_FOUND, result?.statusCode)
    }

    @Test
    @Sql("/db/roles.sql")
    fun shouldReturnBadRequestWhenTryUpdateButRoleSlugExist() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 22, 11, 12, 0, 0)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        val roleDto = NewRoleDto("Role 2", "role_2")
        // when
        val requestEntity = HttpEntity(roleDto)
        val result =
            Assertions.catchThrowableOfType(
                { restApiTemplate.exchange("/roles/role_1", HttpMethod.PUT, requestEntity, RoleDto::class.java) },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.BAD_REQUEST, result?.statusCode)
    }

    @ParameterizedTest
    @MethodSource("provideRoleDtoList")
    fun shouldReturnBadRequestWhenTryUpdate(roleName: String?, roleSlug: String?, message: String) {
        // given
        val roleDto = NewRoleDto(roleName, roleSlug)
        // when
        val result =
            Assertions.catchThrowableOfType(
                { restApiTemplate.put("/roles/role-1", roleDto, Any::class.java) },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.BAD_REQUEST, result!!.statusCode)
        assertTrue(result.message!!.contains(message))
    }

    @ParameterizedTest
    @MethodSource("provideRoleDtoValidList")
    @Sql("/db/roles.sql")
    fun shouldUpdate(roleName: String, roleSlug: String?, expectedRoleSlug: String) {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 28, 7, 48, 0, 0)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        val roleDto = NewRoleDto(roleName, roleSlug)
        // when
        val requestEntity = HttpEntity(roleDto)
        val role: ResponseEntity<RoleDto> =
            restApiTemplate.exchange("/roles/role_1", HttpMethod.PUT, requestEntity, RoleDto::class.java)
        // then
        assertNotNull(role)
        assertEquals(HttpStatus.OK, role.statusCode)
        Assertions.assertThat(role.body)
            .hasFieldOrPropertyWithValue("name", roleName.uppercase())
            .hasFieldOrPropertyWithValue("slug", expectedRoleSlug)
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 28, 7, 34, 0, 0))
            .hasFieldOrPropertyWithValue("updatedAt", localDateTime)
    }

    @Test
    @Sql("/db/roles.sql")
    fun shouldDelete() {
        // given
        // when
        val result = restApiTemplate.exchange("/roles/role-1", HttpMethod.DELETE, null, Any::class.java)
        // then
        assertEquals(HttpStatus.OK, result.statusCode)
        val roleEntity = dbUtils.em().find(RoleEntity::class.java, "role-1")
        assertNull(roleEntity)
    }

    companion object {
        @JvmStatic
        private fun provideRoleDtoList(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(null, null, "newRoleDto.name"),
                Arguments.of("Na", null, "newRoleDto.name"),
                Arguments.of("Role", "ro", "newRoleDto.slug"),
                Arguments.of("", null, "newRoleDto.name"),
                Arguments.of(null, "", "newRoleDto.name"),
                Arguments.of("", "", "newRoleDto.name"),
                Arguments.of(
                    "VeryLongRoleNameVeryLongRoleNameVeryLongRoleNameVeryLongRoleName",
                    null,
                    "newRoleDto.name"
                ),
                Arguments.of(
                    "Role Ok",
                    "VeryLongRoleNameVeryLongRoleNameVeryLongRoleNameVeryLongRoleName",
                    "newRoleDto.slug"
                )
            )
        }

        @JvmStatic
        private fun provideRoleDtoValidList(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("NewRoleName", null, "newrolename"),
                Arguments.of("NewRoleName", "new-role-name", "new-role-name"),
                Arguments.of("Role 2", null, "role_21"),
            )
        }
    }
}