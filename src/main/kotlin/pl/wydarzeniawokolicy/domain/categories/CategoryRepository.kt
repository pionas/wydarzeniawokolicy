package pl.wydarzeniawokolicy.domain.categories

import pl.wydarzeniawokolicy.domain.categories.api.Category
import pl.wydarzeniawokolicy.domain.categories.api.CategoryFilter
import pl.wydarzeniawokolicy.domain.shared.SlugRepository
import java.util.*

interface CategoryRepository : SlugRepository {

    fun findAll(): List<Category>
    fun findBySlug(slug: String): Category?
    fun create(category: Category): Category
    fun update(currentSlug: String, category: Category): Category
    fun delete(slug: String)
    fun findAll(filter: CategoryFilter): List<Category>
    fun count(filter: CategoryFilter): Int

}
