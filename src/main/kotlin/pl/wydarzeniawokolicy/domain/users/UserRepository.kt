package pl.wydarzeniawokolicy.domain.users

import pl.wydarzeniawokolicy.domain.users.api.User
import java.util.*

interface UserRepository {

    fun findAll(): List<User>
    fun save(user: User): User
    fun findById(userId: Long): Optional<User>
    fun deleteById(userId: Long)
    fun existsByName(name: String): Boolean
    fun existsByEmail(email: String): Boolean
}