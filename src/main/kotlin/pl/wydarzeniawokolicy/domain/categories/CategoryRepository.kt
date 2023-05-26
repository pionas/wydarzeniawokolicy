package pl.wydarzeniawokolicy.domain.categories

import pl.wydarzeniawokolicy.domain.categories.api.Category
import pl.wydarzeniawokolicy.domain.shared.SlugRepository

interface CategoryRepository : SlugRepository {

    fun findAll(): List<Category>
    fun findBySlug(slug: String): Category?
    fun create(category: Category): Category
    fun delete(slug: String)

}
