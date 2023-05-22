package pl.wydarzeniawokolicy.infrastructure.database.users

import org.springframework.stereotype.Repository
import pl.wydarzeniawokolicy.domain.users.UserRepository
import java.time.LocalDateTime
import java.util.*

@Repository
class UserRepositoryImpl(val userJpaRepository: UserJpaRepository) : UserRepository {
    override fun findAll(): List<User> {
        return userJpaRepository.findAll()
            .map { User(it) }
    }

    override fun save(user: User): User {
        val userEntity = UserEntity(user.id, user.name, user.email, "", "", LocalDateTime.now(), null, null)
        return User(userJpaRepository.save(userEntity))
    }

    override fun findById(userId: Long): Optional<User> {
        return userJpaRepository.findById(userId)
            .map { User(it) }
    }

    override fun deleteById(userId: Long) {
        userJpaRepository.deleteById(userId)
    }
}