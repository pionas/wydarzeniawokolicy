package pl.wydarzeniawokolicy.infrastructure.database.categories

import org.springframework.stereotype.Repository
import pl.wydarzeniawokolicy.domain.categories.CategoryRepository
import pl.wydarzeniawokolicy.domain.categories.api.Category
import pl.wydarzeniawokolicy.domain.categories.api.CategoryFilter
import java.util.*

@Repository
class CategoryRepositoryImpl(val repository: CategoryJpaRepository) : CategoryRepository {

    override fun findAll(): List<Category> {
        return repository.findAll()
            .map { Category(it) }
    }

    override fun findAll(filter: CategoryFilter): List<Category> {
        return repository.findAll(
            CategorySpecification.slugEquals(filter.searchPhrase).or(CategorySpecification.nameEquals(filter.searchPhrase)),
            filter.pageable!!
        ).get()
            .map { Category(it) }
            .toList()
    }

    override fun findBySlug(slug: String): Category? {
        return repository.findById(slug)
            .map { Category(it) }
            .orElse(null)
    }

    override fun create(category: Category): Category {
        val categoryEntity = CategoryEntity(
            name = category.name,
            slug = category.slug,
            createdAt = category.createdAt,
            updatedAt = category.updatedAt,
            deletedAt = category.deletedAt
        )
        return Category(repository.save(categoryEntity))
    }

    override fun update(currentSlug: String, category: Category): Category {
        repository.update(
            category.slug,
            category.name,
            category.createdAt,
            category.updatedAt,
            category.deletedAt,
            currentSlug
        )
        return Category(
            category.name,
            category.slug,
            category.createdAt,
            category.updatedAt,
            category.deletedAt
        )
    }

    override fun existsBySlug(slug: String): Boolean {
        return repository.existsBySlug(slug)
    }

    override fun delete(slug: String) {
        repository.deleteById(slug)
    }

    override fun count(filter: CategoryFilter): Int {
        val search = filter.searchPhrase
        return repository.count(
            CategorySpecification.slugEquals(search).or(CategorySpecification.nameEquals(search))
        ).toInt()
    }
}