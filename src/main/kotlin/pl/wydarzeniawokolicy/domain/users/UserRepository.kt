package pl.wydarzeniawokolicy.domain.users

import pl.wydarzeniawokolicy.domain.users.api.User

interface UserRepository {

    fun findAll(): List<User>
    fun save(user: User): User
    fun findById(userId: Long): User?
    fun findByName(name: String): User?
    fun findByEmail(email: String): User?
    fun deleteById(userId: Long)
}