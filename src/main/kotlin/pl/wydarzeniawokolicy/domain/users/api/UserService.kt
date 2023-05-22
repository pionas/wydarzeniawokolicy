package pl.wydarzeniawokolicy.domain.users.api

import java.util.*

interface UserService {

    fun findAll(): List<User>
    fun create(user: UserSignUp): User
    fun update(userDetails: UserDetails): User
    fun findById(userId: Long): User
    fun deleteById(userId: Long)
}