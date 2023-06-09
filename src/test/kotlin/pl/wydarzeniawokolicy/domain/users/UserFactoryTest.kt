package pl.wydarzeniawokolicy.domain.users

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowableOfType
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import pl.wydarzeniawokolicy.domain.roles.RoleRepository
import pl.wydarzeniawokolicy.domain.roles.api.Role
import pl.wydarzeniawokolicy.domain.shared.DateTimeUtils
import pl.wydarzeniawokolicy.domain.shared.NotFoundException
import pl.wydarzeniawokolicy.domain.shared.StringUtils
import pl.wydarzeniawokolicy.domain.users.api.*
import java.time.LocalDateTime

class UserFactoryTest {

    private val userRepository: UserRepository = mock()
    private val roleRepository: RoleRepository = mock()
    private val dateTimeUtils: DateTimeUtils = mock()
    private val stringUtils: StringUtils = mock()
    private val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()
    private val userFactory: UserFactory =
        UserFactory(userRepository, roleRepository, dateTimeUtils, passwordEncoder, stringUtils)

    @Test
    fun shouldThrowExceptionWhenUserByNameExist() {
        // given
        val userSignUp = getUserSignUp(password = "password", passwordConfirm = "passwordConfirm")
        whenever(userRepository.findByName(userSignUp.name)).thenReturn(getUser(1L, null, "name", "email"))
        // when
        val exception =
            catchThrowableOfType({ userFactory.create(userSignUp) }, UserNameExistException::class.java)
        // then
        assertThat(exception.message).isEqualTo("User by name name is exist!")
    }

    @Test
    fun shouldThrowExceptionWhenUserByEmailExist() {
        // given
        val userSignUp = getUserSignUp(password = "password", passwordConfirm = "passwordConfirm")
        whenever(userRepository.findByName(userSignUp.name)).thenReturn(null)
        whenever(userRepository.findByEmail(userSignUp.email)).thenReturn(getUser(1L, null, "name", "email"))
        // when
        val exception =
            catchThrowableOfType({ userFactory.create(userSignUp) }, UserEmailExistException::class.java)
        // then
        assertThat(exception.message).isEqualTo("User by email email is exist!")
    }

    @Test
    fun shouldThrowExceptionWhenUserPasswordNotMatches() {
        // given
        val userSignUp = getUserSignUp(password = "password", passwordConfirm = "passwordConfirm")
        whenever(userRepository.findByName(userSignUp.name)).thenReturn(null)
        whenever(userRepository.findByEmail(userSignUp.email)).thenReturn(null)
        // when
        val exception =
            catchThrowableOfType({ userFactory.create(userSignUp) }, UserPasswordCompareException::class.java)
        // then
        assertThat(exception.message).isEqualTo("Passwords do not match")
    }

    @Test
    fun shouldThrowExceptionWhenRoleNotExist() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 22, 11, 12, 0, 0)
        val salt = "salt"
        val userSignUp = getUserSignUp("validPassword", "validPassword", listOf("ADMIN"))
        whenever(userRepository.findByName(userSignUp.name)).thenReturn(null)
        whenever(userRepository.findByEmail(userSignUp.email)).thenReturn(null)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        whenever(stringUtils.randomAlphanumeric(10)).thenReturn(salt)
        // when
        val exception =
            catchThrowableOfType({ userFactory.create(userSignUp) }, NotFoundException::class.java)
        // then
        assertThat(exception.message).isEqualTo("Role by slug ADMIN not exist")
    }

    @Test
    fun shouldCreateUserWithoutRoles() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 22, 11, 12, 0, 0)
        val salt = "salt"
        val userSignUp = getUserSignUp("validPassword", "validPassword")
        whenever(userRepository.findByName(userSignUp.name)).thenReturn(null)
        whenever(userRepository.findByEmail(userSignUp.email)).thenReturn(null)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        whenever(stringUtils.randomAlphanumeric(10)).thenReturn(salt)
        // when
        val user = userFactory.create(userSignUp)
        // then
        assertThat(user)
            .hasFieldOrPropertyWithValue("id", null)
            .hasFieldOrPropertyWithValue("name", "name")
            .hasFieldOrPropertyWithValue("email", "email")
            .hasFieldOrPropertyWithValue("salt", salt)
            .hasFieldOrPropertyWithValue("createdAt", localDateTime)
        assertThat(passwordEncoder.matches(userSignUp.password.plus(salt), user.password)).isTrue
    }

    @Test
    fun shouldCreateUserWithRoles() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 22, 11, 12, 0, 0)
        val salt = "salt"
        val userSignUp = getUserSignUp("validPassword", "validPassword", listOf("ADMIN"))
        whenever(userRepository.findByName(userSignUp.name)).thenReturn(null)
        whenever(userRepository.findByEmail(userSignUp.email)).thenReturn(null)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        whenever(stringUtils.randomAlphanumeric(10)).thenReturn(salt)
        userSignUp.roles?.forEach {
            whenever(roleRepository.findBySlug(it.lowercase())).thenReturn(getRole(it))
        }
        // when
        val user = userFactory.create(userSignUp)
        // then
        assertThat(user)
            .hasFieldOrPropertyWithValue("id", null)
            .hasFieldOrPropertyWithValue("name", "name")
            .hasFieldOrPropertyWithValue("email", "email")
            .hasFieldOrPropertyWithValue("salt", salt)
            .hasFieldOrPropertyWithValue("salt", salt)
            .hasFieldOrPropertyWithValue("createdAt", localDateTime)
        assertThat(passwordEncoder.matches(userSignUp.password.plus(salt), user.password)).isTrue
    }

    @Test
    fun shouldThrowExceptionWhenUserNotExist() {
        // given
        val userId = 1L
        val userDetails = getUserDetails("newPassword", "passwordConfirm")
        whenever(userRepository.findById(userId)).thenReturn(null)
        // when
        val exception =
            catchThrowableOfType({ userFactory.update(userId, userDetails) }, UserNotFoundException::class.java)
        // then
        assertThat(exception.message).isEqualTo("User by ID $userId not exists")
    }

    @Test
    fun shouldThrowExceptionWhenTryUpdateButUserByNameExist() {
        // given
        val userId = 1L
        val userDetails = getUserDetails("newPassword", "passwordConfirm")
        whenever(userRepository.findById(userId)).thenReturn(getUser(userId, null, "name", "email"))
        whenever(userRepository.findByName(userDetails.name)).thenReturn(getUser(2L, null, "name", "email"))
        // when
        val exception =
            catchThrowableOfType({ userFactory.update(userId, userDetails) }, UserNameExistException::class.java)
        // then
        assertThat(exception.message).isEqualTo("User by name newName is exist!")
    }

    @Test
    fun shouldThrowExceptionWhenTryUpdateButUserByEmailExist() {
        // given
        val userId = 1L
        val userDetails = getUserDetails("newPassword", "passwordConfirm")
        whenever(userRepository.findById(userId)).thenReturn(getUser(userId, null, "name", "email"))
        whenever(userRepository.findByName(userDetails.name)).thenReturn(null)
        whenever(userRepository.findByEmail(userDetails.email)).thenReturn(getUser(2L, null, "name", "email"))
        // when
        val exception =
            catchThrowableOfType({ userFactory.update(userId, userDetails) }, UserEmailExistException::class.java)
        // then
        assertThat(exception.message).isEqualTo("User by email newEmail is exist!")
    }

    @Test
    fun shouldThrowExceptionWhenTryUpdateButUserPasswordNotMatches() {
        // given
        val userId = 1L
        val userDetails = getUserDetails("newPassword", "passwordConfirm")
        whenever(userRepository.findById(userId)).thenReturn(getUser(1L, null, "name", "email"))
        whenever(userRepository.findByName(userDetails.name)).thenReturn(null)
        whenever(userRepository.findByEmail(userDetails.email)).thenReturn(null)
        // when
        val exception =
            catchThrowableOfType({ userFactory.update(userId, userDetails) }, UserPasswordCompareException::class.java)
        // then
        assertThat(exception.message).isEqualTo("Passwords do not match")
    }

    @Test
    fun shouldThrowExceptionWhenTryUpdateButUserOldPasswordIsWrong() {
        // given
        val userId = 1L
        val userDetails = getUserDetails("password", "password")
        val localDateTime = LocalDateTime.of(2023, 5, 22, 11, 12, 0, 0)
        whenever(userRepository.findById(userId)).thenReturn(getUser(userId, null, "name", "email"))
        whenever(userRepository.findByName(userDetails.name)).thenReturn(null)
        whenever(userRepository.findByEmail(userDetails.email)).thenReturn(null)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        // when
        val exception =
            catchThrowableOfType({ userFactory.update(userId, userDetails) }, UserInvalidPasswordException::class.java)
        // then
        assertThat(exception.message).isEqualTo("Invalid password")
    }

    @Test
    fun shouldThrowExceptionWhenTryUpdateButRoleNotExist() {
        // given
        val userId = 1L
        val salt = "newSalt"
        val userDetails = getUserDetails("password", "password", listOf("Manager"))
        val localDateTime = LocalDateTime.of(2023, 5, 22, 19, 3, 0, 0)
        whenever(userRepository.findById(userId)).thenReturn(
            getUser(
                userId,
                "\$2a\$10\$xAsTwpeJHG.nAfBZsyfrCOWJOfsZ2lZ3k73qeYP8eGMLeusQ.JlFi",
                "name",
                "email"
            )
        )
        whenever(userRepository.findByName(userDetails.name)).thenReturn(null)
        whenever(userRepository.findByEmail(userDetails.email)).thenReturn(null)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        whenever(stringUtils.randomAlphanumeric(10)).thenReturn(salt)
        // when
        val exception =
            catchThrowableOfType({ userFactory.update(userId, userDetails) }, NotFoundException::class.java)
        // then
        assertThat(exception.message).isEqualTo("Role by slug Manager not exist")
    }

    @Test
    fun shouldUpdateUserWithoutRoles() {
        // given
        val userId = 1L
        val salt = "newSalt"
        val userDetails = getUserDetails("password", "password")
        val localDateTime = LocalDateTime.of(2023, 5, 22, 19, 3, 0, 0)
        whenever(userRepository.findById(userId)).thenReturn(
            getUser(
                userId,
                "\$2a\$10\$xAsTwpeJHG.nAfBZsyfrCOWJOfsZ2lZ3k73qeYP8eGMLeusQ.JlFi",
                "name",
                "email",
                listOf("Admin")
            )
        )
        whenever(userRepository.findByName(userDetails.name)).thenReturn(null)
        whenever(userRepository.findByEmail(userDetails.email)).thenReturn(null)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        whenever(stringUtils.randomAlphanumeric(10)).thenReturn(salt)
        // when
        userFactory.update(userId, userDetails)
        // then
        val argumentCaptor = argumentCaptor<User>()
        verify(userRepository, times(1)).save(argumentCaptor.capture())
        assertThat(argumentCaptor.firstValue)
            .hasFieldOrPropertyWithValue("id", userId)
            .hasFieldOrPropertyWithValue("name", userDetails.name)
            .hasFieldOrPropertyWithValue("email", userDetails.email)
            .hasFieldOrPropertyWithValue("salt", salt)
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 22, 12, 10, 0, 0))
            .hasFieldOrPropertyWithValue("updatedAt", localDateTime)
        assertThat(
            passwordEncoder.matches(
                userDetails.password.plus(salt),
                "\$2a\$10\$bWf3STnY/vrO5xgF2EOlm.qsrra4GG30s5PuH3ttm3PfGWtJa79KW"
            )
        ).isTrue
    }

    @Test
    fun shouldUpdateUserWithRoles() {
        // given
        val userId = 1L
        val salt = "newSalt"
        val userDetails = getUserDetails("password", "password", listOf("Manager"))
        val localDateTime = LocalDateTime.of(2023, 5, 22, 19, 3, 0, 0)
        whenever(userRepository.findById(userId)).thenReturn(
            getUser(
                userId,
                "\$2a\$10\$xAsTwpeJHG.nAfBZsyfrCOWJOfsZ2lZ3k73qeYP8eGMLeusQ.JlFi",
                "name",
                "email"
            )
        )
        whenever(userRepository.findByName(userDetails.name)).thenReturn(null)
        whenever(userRepository.findByEmail(userDetails.email)).thenReturn(null)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        whenever(stringUtils.randomAlphanumeric(10)).thenReturn(salt)
        userDetails.roles?.forEach {
            whenever(roleRepository.findBySlug(it.lowercase())).thenReturn(getRole(it))
        }
        // when
        userFactory.update(userId, userDetails)
        // then
        val argumentCaptor = argumentCaptor<User>()
        verify(userRepository, times(1)).save(argumentCaptor.capture())
        assertThat(argumentCaptor.firstValue)
            .hasFieldOrPropertyWithValue("id", userId)
            .hasFieldOrPropertyWithValue("name", userDetails.name)
            .hasFieldOrPropertyWithValue("email", userDetails.email)
            .hasFieldOrPropertyWithValue("salt", salt)
            .hasFieldOrPropertyWithValue("roles", listOf(getRole("Manager")))
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 22, 12, 10, 0, 0))
            .hasFieldOrPropertyWithValue("updatedAt", localDateTime)
        assertThat(
            passwordEncoder.matches(
                userDetails.password.plus(salt),
                "\$2a\$10\$bWf3STnY/vrO5xgF2EOlm.qsrra4GG30s5PuH3ttm3PfGWtJa79KW"
            )
        ).isTrue
    }


    private fun getUserSignUp(password: String, passwordConfirm: String, roles: List<String>? = null): UserSignUp =
        UserSignUp("name", "email", password, passwordConfirm, roles)

    private fun getUser(
        userId: Long,
        password: String?,
        name: String,
        email: String,
        roles: List<String>? = emptyList()
    ): User {
        val roleList = roles?.map { getRole(it) }
        return User(
            userId,
            name,
            email,
            password,
            "salt",
            roleList,
            LocalDateTime.of(2023, 5, 22, 12, 10, 0, 0),
            null,
            null
        )
    }

    private fun getUserDetails(
        password: String,
        passwordConfirm: String,
        roles: List<String>? = emptyList()
    ): UserDetails =
        UserDetails("newName", "newEmail", "oldPassword", password, passwordConfirm, roles)

    private fun getRole(
        slug: String
    ) = Role(slug, slug, LocalDateTime.of(2023, 6, 9, 13, 59, 20, 0), null, null)
}