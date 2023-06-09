package pl.wydarzeniawokolicy.domain.roles.api

interface RoleService {

    fun findAll(): List<Role>
    fun create(newRole: NewRole): Role
    fun delete(slug: String)
    fun update(currentSlug: String, newRole: NewRole): Role
    fun findBySlug(slug: String): Role

}
