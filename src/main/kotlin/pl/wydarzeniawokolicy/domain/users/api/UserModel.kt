package pl.wydarzeniawokolicy.domain.users.api

import pl.wydarzeniawokolicy.domain.shared.BasicModel
import pl.wydarzeniawokolicy.infrastructure.database.users.UserEntity
import java.time.LocalDateTime

class UserDetails(
    val name: String,
    val email: String,
    val oldPassword: String,
    val password: String,
    val passwordConfirm: String
) {
    fun validPassword(): Boolean = password == passwordConfirm
}

class UserSignUp(val name: String, val email: String, val password: String, val passwordConfirm: String) {
    fun validPassword(): Boolean = password == passwordConfirm
}

class User(
    val id: Long?, var name: String, var email: String, var password: String?, var salt: String?,
    createdAt: LocalDateTime,
    updatedAt: LocalDateTime?,
    deletedAt: LocalDateTime?
) : BasicModel(createdAt, updatedAt, deletedAt) {

    fun update(user: User): User {
        this.name = user.name
        this.email = user.email
        return this
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