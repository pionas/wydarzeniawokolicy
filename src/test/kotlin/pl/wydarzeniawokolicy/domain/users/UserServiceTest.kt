package pl.wydarzeniawokolicy.domain.users

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import pl.wydarzeniawokolicy.domain.users.api.User
import pl.wydarzeniawokolicy.domain.users.api.UserService
import java.time.LocalDateTime

class UserServiceTest() {

    private val repository = Mockito.mock(UserRepository::class.java)
    private val userFactory = Mockito.mock(UserFactory::class.java)
    private val service: UserService = UserServiceImpl(repository, userFactory)

    @Test
    fun shouldReturnEmptyList() {
        // given
        Mockito.`when`(repository.findAll()).thenReturn(emptyList())
        // when
        val users = service.findAll()
        // then
        Assertions.assertThat(users).hasSize(0)
    }

    @Test
    fun shouldReturnListOfUser() {
        // given
        Mockito.`when`(repository.findAll()).thenReturn(userList())
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
    }

    @Test
    fun create() {

    }

    @Test
    fun update() {
    }

    @Test
    fun findById() {
    }

    @Test
    fun deleteById() {
    }

    private fun userList(): List<User> {
        return listOf(
            getUser(
                1,
                "john.doe",
                "john.doe@example.com",
                "johnPassword",
                "johnSalt",
                LocalDateTime.of(2023, 5, 22, 11, 12, 0, 0)
            ),
            getUser(
                2, "anna.doe", "anna.doe@example.com", "annaPassword", "annaSalt",
                LocalDateTime.of(2023, 5, 22, 11, 12, 10, 0)
            ),
            getUser(
                3, "jane.doe", "jane.doe@example.com", "janePassword", "janeSalt",
                LocalDateTime.of(2023, 5, 22, 11, 12, 20, 0)
            ),
        )
    }

    private fun getUser(
        id: Long?,
        name: String,
        email: String,
        password: String?,
        salt: String?,
        createdAt: LocalDateTime
    ): User {
        return User(id, name, email, password, salt, createdAt, null, null)
    }
}