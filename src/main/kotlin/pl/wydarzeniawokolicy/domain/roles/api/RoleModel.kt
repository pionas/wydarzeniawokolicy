package pl.wydarzeniawokolicy.domain.roles.api

import pl.wydarzeniawokolicy.domain.shared.BasicModel
import pl.wydarzeniawokolicy.infrastructure.database.roles.RoleEntity
import java.time.LocalDateTime

class NewRole(val name: String, var slug: String?)
class Role(
    var name: String,
    var slug: String,
    createdAt: LocalDateTime,
    updatedAt: LocalDateTime? = null,
    deletedAt: LocalDateTime? = null
) : BasicModel(createdAt, updatedAt, deletedAt) {
    fun update(name: String, slug: String, localDateTimeNow: LocalDateTime) {
        this.name = name
        this.slug = slug
        this.updatedAt = localDateTimeNow
    }

    constructor(entity: RoleEntity) : this(
        slug = entity.slug,
        name = entity.name,
        createdAt = entity.createdAt,
        updatedAt = entity.updatedAt,
        deletedAt = entity.deletedAt
    )
}