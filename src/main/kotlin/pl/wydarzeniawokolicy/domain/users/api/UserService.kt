package pl.wydarzeniawokolicy.domain.users.api

import java.util.*

interface UserService {

    fun findAll(): List<User>
    fun save(user: User): User
    fun findById(userId: Long): Optional<User>
    fun deleteById(userId: Long)
}