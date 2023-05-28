package pl.wydarzeniawokolicy.domain.roles

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import pl.wydarzeniawokolicy.domain.roles.api.NewRole
import pl.wydarzeniawokolicy.domain.roles.api.Role
import pl.wydarzeniawokolicy.domain.roles.api.RoleService
import pl.wydarzeniawokolicy.domain.shared.DateTimeUtils
import pl.wydarzeniawokolicy.domain.shared.ModelException
import pl.wydarzeniawokolicy.domain.shared.NotFoundException
import pl.wydarzeniawokolicy.domain.shared.StringUtils
import java.time.LocalDateTime

class RoleServiceTest {

    private val dateTimeUtils: DateTimeUtils = mock()
    private val repository: RoleRepository = mock()
    private val stringUtils: StringUtils = mock()
    private val service: RoleService = RoleServiceImpl(repository, dateTimeUtils, stringUtils)

    @Test
    fun shouldReturnEmptyList() {
        // given
        whenever(repository.findAll()).thenReturn(emptyList())
        // when
        val roles = service.findAll()
        // then
        Assertions.assertThat(roles).hasSize(0)
        verify(repository, times(1)).findAll()
    }

    @Test
    fun shouldReturnRoleList() {
        // given
        whenever(repository.findAll()).thenReturn(getRoleList())
        // when
        val roles = service.findAll()
        // then
        Assertions.assertThat(roles).hasSize(3)
        verify(repository, times(1)).findAll()
        Assertions.assertThat(roles[0])
            .hasFieldOrPropertyWithValue("name", "role 1")
            .hasFieldOrPropertyWithValue("slug", "role-1")
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 28, 7, 34, 0, 0))
        Assertions.assertThat(roles[1])
            .hasFieldOrPropertyWithValue("name", "role 2")
            .hasFieldOrPropertyWithValue("slug", "role-2")
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 28, 7, 34, 10, 0))
        Assertions.assertThat(roles[2])
            .hasFieldOrPropertyWithValue("name", "role 3")
            .hasFieldOrPropertyWithValue("slug", "role-3")
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 28, 7, 34, 20, 0))
    }

    @Test
    fun shouldCreateRoleWithSlug() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 28, 7, 34, 0, 0)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        whenever(repository.findBySlug(any())).thenReturn(null)
        val role = NewRole("Role 1", "role-1")
        // when
        service.create(role)
        // then
        val argumentCaptor = argumentCaptor<Role>()
        verify(repository, times(1)).create(argumentCaptor.capture())
        verify(repository, times(1)).findBySlug(any())
        Assertions.assertThat(argumentCaptor.firstValue)
            .hasFieldOrPropertyWithValue("name", "Role 1")
            .hasFieldOrPropertyWithValue("slug", "role-1")
            .hasFieldOrPropertyWithValue("createdAt", localDateTime)
    }

    @Test
    fun shouldCreateRoleWithoutSlug() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 28, 7, 34, 0, 0)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        whenever(repository.findBySlug(any())).thenReturn(null)
        whenever(repository.existsBySlug(any())).thenReturn(false)
        whenever(stringUtils.slug(any())).thenReturn("role-1")
        val role = NewRole("Role 1", null)
        // when
        service.create(role)
        // then
        val argumentCaptor = argumentCaptor<Role>()
        verify(repository, times(1)).create(argumentCaptor.capture())
        verify(repository, times(1)).existsBySlug(any())
        Assertions.assertThat(argumentCaptor.firstValue)
            .hasFieldOrPropertyWithValue("name", "Role 1")
            .hasFieldOrPropertyWithValue("slug", "role-1")
            .hasFieldOrPropertyWithValue("createdAt", localDateTime)
    }

    @Test
    fun shouldCreateRoleWithoutSlugAndSlugByRoleNameExist() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 28, 7, 34, 0, 0)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        whenever(repository.findBySlug(any())).thenReturn(null)
        whenever(repository.existsBySlug("role")).thenReturn(true)
        whenever(repository.existsBySlug("role1")).thenReturn(true)
        whenever(repository.existsBySlug("role2")).thenReturn(false)
        whenever(stringUtils.slug(any())).thenReturn("role")
        val role = NewRole("Role", null)
        // when
        service.create(role)
        // then
        val argumentCaptor = argumentCaptor<Role>()
        verify(repository, times(1)).create(argumentCaptor.capture())
        verify(repository, times(3)).existsBySlug(any())
        Assertions.assertThat(argumentCaptor.firstValue)
            .hasFieldOrPropertyWithValue("name", "Role")
            .hasFieldOrPropertyWithValue("slug", "role2")
            .hasFieldOrPropertyWithValue("createdAt", localDateTime)
    }

    @Test
    fun shouldThrowRoleBySlugExistWhenTryCreateRoleWithSlug() {
        // given
        whenever(repository.findBySlug(any())).thenReturn(
            getRole(
                "role 1",
                "role-1",
                LocalDateTime.of(2023, 5, 28, 7, 34, 0, 0),
                null
            )
        )
        val role = NewRole("Role 1", "role-1")
        // when
        val exception =
            Assertions.catchThrowableOfType({ service.create(role) }, ModelException::class.java)
        // then
        Assertions.assertThat(exception.message).isEqualTo("Role by slug ${role.slug} exist")
        verify(repository, times(1)).findBySlug(role.slug!!)
    }

    @Test
    fun shouldUpdateRoleWithSlug() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 28, 7, 34, 0, 0)
        val currentSlug = "role-slug"
        val roleToUpdate = NewRole("Role 1", "role-1")
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime.plusDays(1))
        whenever(repository.existsBySlug(any())).thenReturn(false)
        whenever(repository.findBySlug(currentSlug)).thenReturn(
            getRole(
                "role 1",
                "role-slug",
                localDateTime,
                null
            )
        )
        whenever(repository.findBySlug(roleToUpdate.slug!!)).thenReturn(null)
        // when
        service.update(currentSlug, roleToUpdate)
        //then
        val argumentCaptor = argumentCaptor<Role>()
        verify(repository, times(1)).create(argumentCaptor.capture())
        verify(repository, times(0)).existsBySlug(any())
        Assertions.assertThat(argumentCaptor.firstValue)
            .hasFieldOrPropertyWithValue("name", roleToUpdate.name)
            .hasFieldOrPropertyWithValue("slug", roleToUpdate.slug)
            .hasFieldOrPropertyWithValue("createdAt", localDateTime)
            .hasFieldOrPropertyWithValue("updatedAt", localDateTime.plusDays(1))
    }

    @Test
    fun shouldUpdateRoleWithoutSlug() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 28, 7, 34, 0, 0)
        val currentSlug = "role-slug"
        val roleToUpdate = NewRole("Role After Update", null)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime.plusDays(1))
        whenever(repository.existsBySlug(any())).thenReturn(false)
        whenever(stringUtils.slug(any())).thenReturn("role-after-update")
        whenever(repository.findBySlug(currentSlug)).thenReturn(
            getRole(
                "role 1",
                "role-slug",
                localDateTime,
                null
            ),
            null
        )
        // when
        service.update(currentSlug, roleToUpdate)
        //then
        val argumentCaptor = argumentCaptor<Role>()
        verify(repository, times(1)).create(argumentCaptor.capture())
        verify(repository, times(1)).existsBySlug(any())
        verify(repository, times(1)).findBySlug(any())
        Assertions.assertThat(argumentCaptor.firstValue)
            .hasFieldOrPropertyWithValue("name", "Role After Update")
            .hasFieldOrPropertyWithValue("slug", "role-after-update")
            .hasFieldOrPropertyWithValue("createdAt", localDateTime)
            .hasFieldOrPropertyWithValue("updatedAt", localDateTime.plusDays(1))
    }

    @Test
    fun shouldThrowNotFoundRoleWhenTryUpdateRoleWithInvalidSlug() {
        // given
        whenever(repository.findBySlug(any())).thenReturn(null)
        val currentSlug = "role-slug"
        val roleToUpdate = NewRole("Role 1", "role-1")
        // when
        val exception =
            Assertions.catchThrowableOfType(
                { service.update(currentSlug, roleToUpdate) },
                NotFoundException::class.java
            )
        // then
        Assertions.assertThat(exception.message).isEqualTo("Role by slug $currentSlug not exist")
        verify(repository, times(1)).findBySlug(currentSlug)
    }

    @Test
    fun shouldThrowRoleBySlugExistWhenTryUpdateRoleWithSlug() {
        // given
        whenever(repository.findBySlug(any())).thenReturn(
            getRole(
                "role 1",
                "role-1",
                LocalDateTime.of(2023, 5, 28, 7, 34, 0, 0),
                null
            )
        )
        val role = NewRole("Role 1", "role-1")
        // when
        val exception =
            Assertions.catchThrowableOfType({ service.create(role) }, ModelException::class.java)
        // then
        Assertions.assertThat(exception.message).isEqualTo("Role by slug ${role.slug} exist")
        verify(repository, times(1)).findBySlug(role.slug!!)
    }

    @Test
    fun shouldUpdateRoleWithoutSlugButSlugFromRoleNameExist() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 28, 7, 34, 0, 0)
        val currentSlug = "role-slug"
        val roleToUpdate = NewRole("Role After Update", null)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime.plusDays(1))
        whenever(repository.existsBySlug(any())).thenReturn(true, true, false)
        whenever(stringUtils.slug(any())).thenReturn("role-after-update")
        whenever(repository.findBySlug(currentSlug)).thenReturn(
            getRole(
                "role 1",
                "role-slug",
                localDateTime,
                localDateTime
            )
        )
        // when
        service.update(currentSlug, roleToUpdate)
        //then
        val argumentCaptor = argumentCaptor<Role>()
        verify(repository, times(1)).create(argumentCaptor.capture())
        verify(repository, times(3)).existsBySlug(any())
        verify(repository, times(1)).findBySlug(any())
        Assertions.assertThat(argumentCaptor.firstValue)
            .hasFieldOrPropertyWithValue("name", "Role After Update")
            .hasFieldOrPropertyWithValue("slug", "role-after-update2")
            .hasFieldOrPropertyWithValue("createdAt", localDateTime)
            .hasFieldOrPropertyWithValue("updatedAt", localDateTime.plusDays(1))
    }

    @Test
    fun shouldReturnUserById() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 28, 7, 34, 0, 0)
        val role = getRole(
            "role 1",
            "role-slug",
            localDateTime,
            localDateTime.plusDays(1)
        )
        whenever(repository.findBySlug(role.slug)).thenReturn(role)
        // when
        val roleDetails = service.findBySlug(role.slug)
        // then
        Assertions.assertThat(roleDetails)
            .hasFieldOrPropertyWithValue("name", role.name)
            .hasFieldOrPropertyWithValue("slug", role.slug)
            .hasFieldOrPropertyWithValue("createdAt", role.createdAt)
            .hasFieldOrPropertyWithValue("updatedAt", role.updatedAt)
        verify(repository, times(1)).findBySlug(any())
    }

    @Test
    fun shouldThrowExceptionWhenUserByIdNotExist() {
        // given
        val slug = "invalid-slug"
        whenever(repository.findBySlug(slug)).thenReturn(null)
        // when
        val exception =
            Assertions.catchThrowableOfType({ service.findBySlug(slug) }, NotFoundException::class.java)
        // then
        Assertions.assertThat(exception.message).isEqualTo("Role by slug $slug not exist")
        verify(repository, times(1)).findBySlug(slug)
    }

    @Test
    fun shouldDeleteRole() {
        // given
        val slug = "role-1"
        // when
        service.delete(slug)
        // then
        verify(repository, times(1)).delete(slug)
    }

    private fun getRoleList(): List<Role> {
        return listOf(
            getRole(
                "role 1",
                "role-1",
                LocalDateTime.of(2023, 5, 28, 7, 34, 0, 0),
                null
            ),
            getRole(
                "role 2",
                "role-2",
                LocalDateTime.of(2023, 5, 28, 7, 34, 10, 0),
                null
            ),
            getRole(
                "role 3",
                "role-3",
                LocalDateTime.of(2023, 5, 28, 7, 34, 20, 0),
                null
            )
        )
    }

    private fun getRole(
        name: String,
        slug: String,
        createdAt: LocalDateTime,
        updatedAt: LocalDateTime?,
    ) = Role(name, slug, createdAt, updatedAt, null)

}