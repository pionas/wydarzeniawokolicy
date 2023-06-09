package pl.wydarzeniawokolicy.domain.users

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import pl.wydarzeniawokolicy.domain.roles.RoleRepository
import pl.wydarzeniawokolicy.domain.roles.api.Role
import pl.wydarzeniawokolicy.domain.roles.api.RoleException
import pl.wydarzeniawokolicy.domain.shared.DateTimeUtils
import pl.wydarzeniawokolicy.domain.shared.StringUtils
import pl.wydarzeniawokolicy.domain.users.api.*
import java.util.*

@Component
class UserFactory(
    val userRepository: UserRepository,
    val roleRepository: RoleRepository,
    val dateTimeUtils: DateTimeUtils,
    val passwordEncoder: PasswordEncoder,
    val stringUtils: StringUtils,
) {
    fun create(userSignUp: UserSignUp): User {
        verifyName(null, userSignUp.name)
        verifyEmail(null, userSignUp.email)
        verifyPassword(userSignUp.password, userSignUp.passwordConfirm)
        val roles = getRoles(userSignUp.roles)
        val salt = stringUtils.randomAlphanumeric(10)
        val password = passwordEncoder.encode(userSignUp.password.plus(salt))
        return User(
            null,
            userSignUp.name,
            userSignUp.email,
            password,
            salt,
            roles,
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
        val roles = getRoles(userDetails.roles)
        user.update(userDetails,roles, stringUtils, passwordEncoder, dateTimeUtils.getLocalDateTimeNow())
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

    private fun getRoles(roles: List<String>?): List<Role> {
        val roleList: MutableList<Role> = arrayListOf()
        roles?.forEach {
            roleList.add(roleRepository.findBySlug(it.lowercase()) ?: throw RoleException.slugNotFound(it))
        }
        return roleList
    }

}
