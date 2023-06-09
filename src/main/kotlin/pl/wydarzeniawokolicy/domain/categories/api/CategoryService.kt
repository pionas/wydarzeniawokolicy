package pl.wydarzeniawokolicy.domain.categories.api

interface CategoryService {

    fun findAll(): List<Category>
    fun findAll(filter: CategoryFilter): List<Category>
    fun count(filter: CategoryFilter): Int
    fun create(newCategory: NewCategory): Category
    fun delete(slug: String)
    fun update(currentSlug: String, categoryToUpdate: NewCategory): Category
    fun findBySlug(slug: String): Category

}
