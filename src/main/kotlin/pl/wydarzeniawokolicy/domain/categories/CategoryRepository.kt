package pl.wydarzeniawokolicy.domain.categories

import pl.wydarzeniawokolicy.domain.categories.api.Category

interface CategoryRepository {

    fun findAll(): List<Category>
    fun findBySlug(slug: String): Category?
    fun create(category: Category): Category
    fun existsBySlug(slug: String): Boolean
    fun delete(slug: String)

}
