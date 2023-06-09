package pl.wydarzeniawokolicy.domain.users

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import pl.wydarzeniawokolicy.domain.roles.api.Role
import pl.wydarzeniawokolicy.domain.users.api.*
import java.time.LocalDateTime

class UserServiceTest {

    private val repository: UserRepository = mock()
    private val userFactory: UserFactory = mock()
    private val service: UserService = UserServiceImpl(repository, userFactory)

    @Test
    fun shouldReturnEmptyList() {
        // given
        whenever(repository.findAll()).thenReturn(emptyList())
        // when
        val users = service.findAll()
        // then
        Assertions.assertThat(users).hasSize(0)
        verify(repository, times(1)).findAll()
    }

    @Test
    fun shouldReturnListOfUser() {
        // given
        whenever(repository.findAll()).thenReturn(userList())
        // when
        val users = service.findAll()
        // then
        Assertions.assertThat(users).hasSize(3)
        Assertions.assertThat(users[0])
            .hasFieldOrPropertyWithValue("id", 1L)
            .hasFieldOrPropertyWithValue("name", "john.doe")
            .hasFieldOrPropertyWithValue("email", "john.doe@example.com")
            .hasFieldOrPropertyWithValue("password", "johnPassword")
            .hasFieldOrPropertyWithValue("salt", "johnSalt")
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 22, 11, 12, 0, 0))
        Assertions.assertThat(users[1])
            .hasFieldOrPropertyWithValue("id", 2L)
            .hasFieldOrPropertyWithValue("name", "anna.doe")
            .hasFieldOrPropertyWithValue("email", "anna.doe@example.com")
            .hasFieldOrPropertyWithValue("password", "annaPassword")
            .hasFieldOrPropertyWithValue("salt", "annaSalt")
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 22, 11, 12, 10, 0))
        Assertions.assertThat(users[2])
            .hasFieldOrPropertyWithValue("id", 3L)
            .hasFieldOrPropertyWithValue("name", "jane.doe")
            .hasFieldOrPropertyWithValue("email", "jane.doe@example.com")
            .hasFieldOrPropertyWithValue("password", "janePassword")
            .hasFieldOrPropertyWithValue("salt", "janeSalt")
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 22, 11, 12, 20, 0))
        verify(repository, times(1)).findAll()
    }

    @Test
    fun shouldCreateUserWithoutRoles() {
        // given
        val userSignUp = getUserSignUp()
        val user = getUser(
            1,
            "john.doe",
            "john.doe@example.com",
            "johnPassword",
            "johnSalt",
            emptyList(),
            LocalDateTime.of(2023, 5, 22, 11, 12, 0, 0),
            null
        )
        whenever(userFactory.create(userSignUp)).thenReturn(user)
        // when
        service.create(userSignUp)
        // then
        val argumentCaptor = argumentCaptor<User>()
        verify(repository, times(1)).save(argumentCaptor.capture())
        Assertions.assertThat(argumentCaptor.firstValue)
            .hasFieldOrPropertyWithValue("id", user.id)
            .hasFieldOrPropertyWithValue("name", user.name)
            .hasFieldOrPropertyWithValue("email", user.email)
            .hasFieldOrPropertyWithValue("password", user.password)
            .hasFieldOrPropertyWithValue("salt", user.salt)
            .hasFieldOrPropertyWithValue("roles", user.roles)
            .hasFieldOrPropertyWithValue("createdAt", user.createdAt)
            .hasFieldOrPropertyWithValue("updatedAt", null)
        verify(repository, times(1)).save(any())
    }

    @Test
    fun shouldCreateUserWithRoles() {
        // given
        val userSignUp = getUserSignUp()
        val user = getUser(
            1,
            "john.doe",
            "john.doe@example.com",
            "johnPassword",
            "johnSalt",
            listOf("admin", "category_management"),
            LocalDateTime.of(2023, 5, 22, 11, 12, 0, 0),
            null
        )
        whenever(userFactory.create(userSignUp)).thenReturn(user)
        // when
        service.create(userSignUp)
        // then
        val argumentCaptor = argumentCaptor<User>()
        verify(repository, times(1)).save(argumentCaptor.capture())
        Assertions.assertThat(argumentCaptor.firstValue)
            .hasFieldOrPropertyWithValue("id", user.id)
            .hasFieldOrPropertyWithValue("name", user.name)
            .hasFieldOrPropertyWithValue("email", user.email)
            .hasFieldOrPropertyWithValue("password", user.password)
            .hasFieldOrPropertyWithValue("salt", user.salt)
            .hasFieldOrPropertyWithValue("roles", user.roles)
            .hasFieldOrPropertyWithValue("createdAt", user.createdAt)
            .hasFieldOrPropertyWithValue("updatedAt", null)
        verify(repository, times(1)).save(any())
    }

    @Test
    fun shouldUpdateUserWithoutRoles() {
        // given
        val userDetails = getUserDetails()
        val user = getUser(
            1,
            "john.doe",
            "john.doe@example.com",
            "johnPassword",
            "johnSalt",
            emptyList(),
            LocalDateTime.of(2023, 5, 22, 11, 12, 0, 0),
            LocalDateTime.of(2023, 5, 22, 19, 12, 0, 0)
        )
        whenever(userFactory.update(user.id!!, userDetails)).thenReturn(user)
        // when
        service.update(user.id!!, userDetails)
        // then
        val argumentCaptor = argumentCaptor<User>()
        verify(repository, times(1)).save(argumentCaptor.capture())
        Assertions.assertThat(argumentCaptor.firstValue)
            .hasFieldOrPropertyWithValue("id", user.id)
            .hasFieldOrPropertyWithValue("name", user.name)
            .hasFieldOrPropertyWithValue("email", user.email)
            .hasFieldOrPropertyWithValue("password", user.password)
            .hasFieldOrPropertyWithValue("salt", user.salt)
            .hasFieldOrPropertyWithValue("roles", user.roles)
            .hasFieldOrPropertyWithValue("createdAt", user.createdAt)
            .hasFieldOrPropertyWithValue("updatedAt", user.updatedAt)
        verify(repository, times(1)).save(any())
    }

    @Test
    fun shouldUpdateUserWithRoles() {
        // given
        val userDetails = getUserDetails()
        val user = getUser(
            1,
            "john.doe",
            "john.doe@example.com",
            "johnPassword",
            "johnSalt",
            listOf("admin", "category_management"),
            LocalDateTime.of(2023, 5, 22, 11, 12, 0, 0),
            LocalDateTime.of(2023, 5, 22, 19, 12, 0, 0)
        )
        whenever(userFactory.update(user.id!!, userDetails)).thenReturn(user)
        // when
        service.update(user.id!!, userDetails)
        // then
        val argumentCaptor = argumentCaptor<User>()
        verify(repository, times(1)).save(argumentCaptor.capture())
        Assertions.assertThat(argumentCaptor.firstValue)
            .hasFieldOrPropertyWithValue("id", user.id)
            .hasFieldOrPropertyWithValue("name", user.name)
            .hasFieldOrPropertyWithValue("email", user.email)
            .hasFieldOrPropertyWithValue("password", user.password)
            .hasFieldOrPropertyWithValue("salt", user.salt)
            .hasFieldOrPropertyWithValue("roles", user.roles)
            .hasFieldOrPropertyWithValue("createdAt", user.createdAt)
            .hasFieldOrPropertyWithValue("updatedAt", user.updatedAt)
        verify(repository, times(1)).save(any())
    }

    @Test
    fun shouldReturnUserById() {
        // given
        val user = getUser(
            1,
            "john.doe",
            "john.doe@example.com",
            "johnPassword",
            "johnSalt",
            listOf("admin", "category_management"),
            LocalDateTime.of(2023, 5, 22, 11, 12, 0, 0),
            LocalDateTime.of(2023, 5, 22, 19, 12, 0, 0)
        )
        whenever(repository.findById(user.id!!)).thenReturn(user)
        // when
        val userDetails = service.findById(user.id!!)
        // then
        Assertions.assertThat(userDetails)
            .hasFieldOrPropertyWithValue("id", user.id)
            .hasFieldOrPropertyWithValue("name", user.name)
            .hasFieldOrPropertyWithValue("email", user.email)
            .hasFieldOrPropertyWithValue("salt", user.salt)
            .hasFieldOrPropertyWithValue("roles", user.roles)
            .hasFieldOrPropertyWithValue("password", user.password)
            .hasFieldOrPropertyWithValue("createdAt", user.createdAt)
            .hasFieldOrPropertyWithValue("updatedAt", user.updatedAt)
        verify(repository, times(1)).findById(user.id!!)
    }

    @Test
    fun shouldThrowExceptionWhenUserByIdNotExist() {
        // given
        val userId = 1L
        whenever(repository.findById(userId)).thenReturn(null)
        // when
        val exception =
            Assertions.catchThrowableOfType({ service.findById(userId) }, UserNotFoundException::class.java)
        // then
        Assertions.assertThat(exception.message).isEqualTo("User by ID $userId not exists")
        verify(repository, times(1)).findById(userId)
    }

    @Test
    fun deleteById() {
        // given
        val userId = 1L
        // when
        service.deleteById(userId)
        // then
        verify(repository, times(1)).deleteById(userId)
    }

    private fun userList(): List<User> {
        return listOf(
            getUser(
                1,
                "john.doe",
                "john.doe@example.com",
                "johnPassword",
                "johnSalt",
                listOf("admin", "category_management"),
                LocalDateTime.of(2023, 5, 22, 11, 12, 0, 0),
                null
            ),
            getUser(
                2, "anna.doe", "anna.doe@example.com", "annaPassword", "annaSalt",
                listOf("admin"),
                LocalDateTime.of(2023, 5, 22, 11, 12, 10, 0),
                null
            ),
            getUser(
                3, "jane.doe", "jane.doe@example.com", "janePassword", "janeSalt",
                emptyList(),
                LocalDateTime.of(2023, 5, 22, 11, 12, 20, 0),
                null
            ),
        )
    }

    private fun getUser(
        id: Long?,
        name: String,
        email: String,
        password: String?,
        salt: String?,
        roles: List<String>?,
        createdAt: LocalDateTime,
        updatedAt: LocalDateTime?,
    ): User {
        val roleList = roles?.map { getRole(it) }
        return User(id, name, email, password, salt, roleList, createdAt, updatedAt, null)
    }

    private fun getUserSignUp() = UserSignUp("name", "email", "password", "passwordConfirm")

    private fun getUserDetails() = UserDetails("newName", "newEmail", "oldPassword", "newPassword", "passwordConfirm")

    private fun getRole(
        slug: String
    ) = Role(slug, slug, LocalDateTime.of(2023, 6, 9, 13, 59, 20, 0), null, null)

}