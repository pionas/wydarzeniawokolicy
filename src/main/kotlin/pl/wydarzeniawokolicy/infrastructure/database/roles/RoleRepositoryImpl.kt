package pl.wydarzeniawokolicy.infrastructure.database.roles

import org.springframework.stereotype.Repository
import pl.wydarzeniawokolicy.domain.roles.RoleRepository
import pl.wydarzeniawokolicy.domain.roles.api.Role

@Repository
class RoleRepositoryImpl(val repository: RoleJpaRepository) : RoleRepository {

    override fun findAll(): List<Role> {
        return repository.findAll()
            .map { Role(it) }
    }

    override fun findBySlug(slug: String): Role? {
        return repository.findById(slug)
            .map { Role(it) }
            .orElse(null)
    }

    override fun create(role: Role): Role {
        val roleEntity = RoleEntity(
            name = role.name,
            slug = role.slug,
            createdAt = role.createdAt,
            updatedAt = role.updatedAt,
            deletedAt = role.deletedAt
        )
        return Role(repository.save(roleEntity))
    }

    override fun existsBySlug(slug: String): Boolean {
        return repository.existsBySlug(slug)
    }

    override fun delete(slug: String) {
        repository.deleteById(slug)
    }
}