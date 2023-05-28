package pl.wydarzeniawokolicy.domain.roles

import pl.wydarzeniawokolicy.domain.roles.api.Role
import pl.wydarzeniawokolicy.domain.shared.SlugRepository

interface RoleRepository : SlugRepository {

    fun findAll(): List<Role>
    fun findBySlug(slug: String): Role?
    fun create(role: Role): Role
    fun delete(slug: String)

}