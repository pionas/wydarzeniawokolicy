package pl.wydarzeniawokolicy.domain.categories.api

import pl.wydarzeniawokolicy.domain.shared.BasicModel
import pl.wydarzeniawokolicy.infrastructure.database.categories.CategoryEntity
import java.time.LocalDateTime

class NewCategory(val name: String, var slug: String?)
class Category(
    var name: String, var slug: String?,
    createdAt: LocalDateTime,
    updatedAt: LocalDateTime? = null,
    deletedAt: LocalDateTime? = null
) : BasicModel(createdAt, updatedAt, deletedAt) {

    fun update(name: String, slug: String, localDateTimeNow: LocalDateTime) {
        this.name = name
        this.slug = slug
        this.updatedAt = localDateTimeNow
    }

    constructor(entity: CategoryEntity) : this(
        name = entity.name,
        slug = entity.slug,
        createdAt = entity.createdAt,
        updatedAt = entity.updatedAt,
        deletedAt = entity.deletedAt
    )
}