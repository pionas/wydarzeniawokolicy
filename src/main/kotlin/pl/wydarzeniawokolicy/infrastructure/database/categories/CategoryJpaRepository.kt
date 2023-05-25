package pl.wydarzeniawokolicy.infrastructure.database.categories

import org.springframework.data.repository.CrudRepository

interface CategoryJpaRepository : CrudRepository<CategoryEntity, String> {

    fun existsBySlug(slug: String): Boolean
}