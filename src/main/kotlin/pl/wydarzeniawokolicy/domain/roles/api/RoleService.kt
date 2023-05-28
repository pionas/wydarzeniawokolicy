package pl.wydarzeniawokolicy.domain.roles.api

interface RoleService {

    fun findAll(): List<Role>
    fun create(category: NewRole): Role
    fun delete(slug: String)
    fun update(slug: String, newRole: NewRole): Role
    fun findBySlug(slug: String): Role

}
