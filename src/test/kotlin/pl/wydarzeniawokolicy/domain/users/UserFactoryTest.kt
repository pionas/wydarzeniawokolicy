package pl.wydarzeniawokolicy.domain.users

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowableOfType
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import pl.wydarzeniawokolicy.domain.shared.DateTimeUtils
import pl.wydarzeniawokolicy.domain.shared.StringUtils
import pl.wydarzeniawokolicy.domain.users.api.*
import java.time.LocalDateTime
import java.util.*

class UserFactoryTest {

    private val repository = Mockito.mock(UserRepository::class.java)
    private val dateTimeUtils: DateTimeUtils = Mockito.mock(DateTimeUtils::class.java)
    private val stringUtils: StringUtils = Mockito.mock(StringUtils::class.java)
    private val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()
    private val userFactory: UserFactory = UserFactory(repository, dateTimeUtils, passwordEncoder, stringUtils)

    @Test
    fun shouldThrowExceptionWhenUserByNameExist() {
        // given
        val userSignUp = getUserSignUp("password", "passwordConfirm")
        `when`(repository.findByName(userSignUp.name)).thenReturn(Optional.of(getUser()))
        // when
        val exception =
            catchThrowableOfType({ userFactory.create(userSignUp) }, UserNameExistException::class.java)
        // then
        assertThat(exception.message).isEqualTo("User by name name is exist!")
    }

    @Test
    fun shouldThrowExceptionWhenUserByEmailExist() {
        // given
        val userSignUp = getUserSignUp("password", "passwordConfirm")
        `when`(repository.findByName(userSignUp.name)).thenReturn(Optional.empty())
        `when`(repository.findByEmail(userSignUp.email)).thenReturn(Optional.of(getUser()))
        // when
        val exception =
            catchThrowableOfType({ userFactory.create(userSignUp) }, UserEmailExistException::class.java)
        // then
        assertThat(exception.message).isEqualTo("User by email email is exist!")
    }

    @Test
    fun shouldThrowExceptionWhenUserPasswordNotMatches() {
        // given
        val userSignUp = getUserSignUp("password", "passwordConfirm")
        `when`(repository.findByName(userSignUp.name)).thenReturn(Optional.empty())
        `when`(repository.findByEmail(userSignUp.email)).thenReturn(Optional.empty())
        // when
        val exception =
            catchThrowableOfType({ userFactory.create(userSignUp) }, UserPasswordCompareException::class.java)
        // then
        assertThat(exception.message).isEqualTo("Passwords do not match")
    }

    @Test
    fun shouldCreateUser() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 22, 11, 12, 0, 0)
        val salt = "salt"
        val userSignUp = getUserSignUp("validPassword", "validPassword")
        `when`(repository.findByName(userSignUp.name)).thenReturn(Optional.empty())
        `when`(repository.findByEmail(userSignUp.email)).thenReturn(Optional.empty())
        `when`(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        `when`(stringUtils.randomAlphanumeric(10)).thenReturn(salt)
        // when
        val user = userFactory.create(userSignUp)
        // then
        assertThat(user)
            .hasFieldOrPropertyWithValue("id", null)
            .hasFieldOrPropertyWithValue("name", "name")
            .hasFieldOrPropertyWithValue("email", "email")
            .hasFieldOrPropertyWithValue("salt", salt)
            .hasFieldOrPropertyWithValue("createdAt", localDateTime)
        assertThat(passwordEncoder.matches(userSignUp.password.plus(salt), user.password)).isTrue()
    }

    @Test
    fun update() {
        // TODO
    }

    private fun getUserSignUp(password: String, passwordConfirm: String): UserSignUp =
        UserSignUp("name", "email", password, passwordConfirm)

    private fun getUser(): User =
        User(1L, "name", "email", "password", "salt", LocalDateTime.of(2023, 5, 22, 12, 10, 0, 0), null, null)
}