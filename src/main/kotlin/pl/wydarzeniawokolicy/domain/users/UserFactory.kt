package pl.wydarzeniawokolicy.domain.users

import lombok.AllArgsConstructor
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import pl.wydarzeniawokolicy.domain.shared.DateTimeUtils
import pl.wydarzeniawokolicy.domain.shared.StringUtils
import pl.wydarzeniawokolicy.domain.users.api.*
import java.util.*


@Component
@AllArgsConstructor
class UserFactory(
    val userRepository: UserRepository,
    val dateTimeUtils: DateTimeUtils,
    val passwordEncoder: PasswordEncoder,
    val stringUtils: StringUtils,
) {
    fun create(userSignUp: UserSignUp): User {
        verifyName(null, userSignUp.name)
        verifyEmail(null, userSignUp.email)
        verifyPassword(userSignUp.password, userSignUp.passwordConfirm)
        val salt = stringUtils.randomAlphanumeric(10)
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

    fun update(userId: Long, userDetails: UserDetails): User {
        val user = userRepository.findById(userId) ?: throw UserNotFoundException(userId)
        verifyName(user.id, userDetails.name)
        verifyEmail(user.id, userDetails.email)
        verifyPassword(userDetails.password, userDetails.passwordConfirm)
        verifyCurrentPassword(user, userDetails.oldPassword)
        user.update(userDetails, stringUtils, passwordEncoder, dateTimeUtils.getLocalDateTimeNow())
        return userRepository.save(user)
    }

    private fun verifyName(id: Long?, name: String) {
        val userByName = userRepository.findByName(name) ?: return
        if (id == null) {
            throw UserNameExistException(name)
        }
        if (!Objects.equals(userByName.id, id)) {
            throw UserNameExistException(name)
        }
    }

    private fun verifyEmail(id: Long?, email: String) {
        val userByName = userRepository.findByEmail(email) ?: return
        if (id == null) {
            throw UserEmailExistException(email)
        }
        if (!Objects.equals(userByName.id, id)) {
            throw UserEmailExistException(email)
        }
    }

    private fun verifyPassword(password: String?, passwordConfirm: String?) {
        if (password != passwordConfirm) {
            throw UserPasswordCompareException()
        }
    }

    private fun verifyCurrentPassword(user: User, oldPassword: String) {
        val matches = passwordEncoder.matches(oldPassword.plus(user.salt), user.password)
        if (!matches) {
            throw UserInvalidPasswordException()
        }
    }

}
