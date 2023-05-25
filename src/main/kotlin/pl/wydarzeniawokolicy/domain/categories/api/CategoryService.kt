package pl.wydarzeniawokolicy.domain.categories.api

interface CategoryService {

    fun findAll(): List<Category>
    fun create(category: NewCategory): Category
    fun delete(slug: String)
    fun update(currentSlug: String, categoryToUpdate: NewCategory): Category
    fun findBySlug(slug: String): Category

}
