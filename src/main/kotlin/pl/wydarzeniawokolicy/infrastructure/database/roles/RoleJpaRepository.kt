package pl.wydarzeniawokolicy.infrastructure.database.roles

import org.springframework.data.repository.CrudRepository

interface RoleJpaRepository : CrudRepository<RoleEntity, String> {

    fun existsBySlug(slug: String): Boolean
}