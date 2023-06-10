package pl.wydarzeniawokolicy.infrastructure.database.users

import org.springframework.stereotype.Repository
import pl.wydarzeniawokolicy.domain.roles.api.Role
import pl.wydarzeniawokolicy.domain.users.UserRepository
import pl.wydarzeniawokolicy.domain.users.api.User
import pl.wydarzeniawokolicy.infrastructure.database.roles.RoleEntity
import pl.wydarzeniawokolicy.infrastructure.database.roles.RoleJpaRepository

@Repository
class UserRepositoryImpl(val userJpaRepository: UserJpaRepository, val roleJpaRepository: RoleJpaRepository) :
    UserRepository {
    override fun findAll(): List<User> {
        return userJpaRepository.findAll()
            .map { User(it) }
    }

    override fun save(user: User): User {
        val roles = getRoles(user.roles)
        val userEntity = UserEntity(
            user.id,
            user.name,
            user.email,
            user.password!!,
            user.salt!!,
            roles,
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

    private fun getRoles(roles: List<Role>?): Set<RoleEntity> {
        val list: MutableSet<RoleEntity> = mutableSetOf()
        roles?.map {
            roleJpaRepository.findById(it.slug)
                .map {
                    list.add(it)
                }
        }
        return list
    }
}