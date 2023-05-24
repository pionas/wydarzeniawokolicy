package pl.wydarzeniawokolicy.infrastructure.database.users

import org.springframework.stereotype.Repository
import pl.wydarzeniawokolicy.domain.users.UserRepository
import pl.wydarzeniawokolicy.domain.users.api.User

@Repository
class UserRepositoryImpl(val userJpaRepository: UserJpaRepository) : UserRepository {
    override fun findAll(): List<User> {
        return userJpaRepository.findAll()
            .map { User(it) }
    }

    override fun save(user: User): User {
        val userEntity = UserEntity(
            user.id,
            user.name,
            user.email,
            user.password!!,
            user.salt!!,
            user.createdAt,
            user.updatedAt,
            user.deletedAt
        )
        return User(userJpaRepository.save(userEntity))
    }

    override fun findById(userId: Long): User? {
        return userJpaRepository.findById(userId)
            .map { User(it) }
            .orElse(null)
    }

    override fun findByName(name: String): User? {
        return userJpaRepository.findByName(name)
            .map { User(it) }
            .orElse(null)
    }

    override fun findByEmail(email: String): User? {
        return userJpaRepository.findByEmail(email)
            .map { User(it) }
            .orElse(null)
    }

    override fun deleteById(userId: Long) {
        userJpaRepository.deleteById(userId)
    }
}