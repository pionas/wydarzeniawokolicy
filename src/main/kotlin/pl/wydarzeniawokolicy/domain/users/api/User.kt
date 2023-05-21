package pl.wydarzeniawokolicy.domain.users.api

import pl.wydarzeniawokolicy.infrastructure.database.users.UserEntity

class User(val id: Long, var name: String, var email: String) {

    fun update(user: User): User {
        this.name = user.name
        this.email = user.email
        return this
    }

    constructor(userEntity: UserEntity) : this(
        id = userEntity.id,
        name = userEntity.name,
        email = userEntity.email
    )
}