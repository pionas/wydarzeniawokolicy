package pl.wydarzeniawokolicy.domain.users

import lombok.AllArgsConstructor
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import pl.wydarzeniawokolicy.domain.shared.DateTimeUtils
import pl.wydarzeniawokolicy.domain.users.api.*


@Component
@AllArgsConstructor
class UserFactory(
    val userRepository: UserRepository,
    val dateTimeUtils: DateTimeUtils,
    val passwordEncoder: PasswordEncoder
) {
    fun create(userSignUp: UserSignUp): User {
        if (userRepository.existsByName(userSignUp.name)) {
            throw UserNameExistException(userSignUp.name)
        }

        if (userRepository.existsByEmail(userSignUp.email)) {
            throw UserEmailExistException(userSignUp.email)
        }

        if (!userSignUp.validPassword()) {
            throw UserInvalidPasswordException()
        }
        val salt = RandomStringUtils.randomAlphanumeric(10)
        val password = passwordEncoder.encode(userSignUp.password.plus(salt))
        return User(
            null,
            userSignUp.name,
            userSignUp.email,
            password,
            salt,
            dateTimeUtils.getLocalDateTimeNow(),
            null,
            null
        )
    }

    fun update(userDetails: UserDetails): User {
        TODO("Not yet implemented")
    }

}
