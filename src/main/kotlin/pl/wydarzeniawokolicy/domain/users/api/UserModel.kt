package pl.wydarzeniawokolicy.domain.users.api

import org.springframework.security.crypto.password.PasswordEncoder
import pl.wydarzeniawokolicy.domain.shared.BasicModel
import pl.wydarzeniawokolicy.domain.shared.StringUtils
import pl.wydarzeniawokolicy.infrastructure.database.users.UserEntity
import java.time.LocalDateTime

class UserDetails(
    val name: String,
    val email: String,
    val oldPassword: String,
    val password: String?,
    val passwordConfirm: String?
)

class UserSignUp(val name: String, val email: String, val password: String, val passwordConfirm: String)

class User(
    val id: Long?, var name: String, var email: String, var password: String?, var salt: String?,
    createdAt: LocalDateTime,
    updatedAt: LocalDateTime?,
    deletedAt: LocalDateTime?
) : BasicModel(createdAt, updatedAt, deletedAt) {

    fun update(
        user: UserDetails,
        stringUtils: StringUtils,
        passwordEncoder: PasswordEncoder,
        localDateTimeNow: LocalDateTime
    ) {
        this.name = user.name
        this.email = user.email
        this.updatedAt = localDateTimeNow
        user.password?.let {
            this.salt = stringUtils.randomAlphanumeric(10)
            this.password = passwordEncoder.encode(it.plus(this.salt))
        }
    }

    constructor(userEntity: UserEntity) : this(
        id = userEntity.id,
        name = userEntity.name,
        email = userEntity.email,
        password = userEntity.password,
        salt = userEntity.salt,
        createdAt = userEntity.createdAt,
        updatedAt = userEntity.updatedAt,
        deletedAt = userEntity.deletedAt
    )
}