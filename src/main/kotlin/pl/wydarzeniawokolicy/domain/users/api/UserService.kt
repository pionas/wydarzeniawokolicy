package pl.wydarzeniawokolicy.domain.users.api

interface UserService {

    fun findAll(): List<User>
    fun create(user: UserSignUp): User
    fun update(userId: Long, userDetails: UserDetails): User
    fun findById(userId: Long): User
    fun deleteById(userId: Long)
}