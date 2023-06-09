package pl.wydarzeniawokolicy.domain.categories

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.wydarzeniawokolicy.domain.categories.api.Category
import pl.wydarzeniawokolicy.domain.categories.api.CategoryException
import pl.wydarzeniawokolicy.domain.categories.api.CategoryService
import pl.wydarzeniawokolicy.domain.categories.api.NewCategory
import pl.wydarzeniawokolicy.domain.shared.DateTimeUtils
import pl.wydarzeniawokolicy.domain.shared.SlugHelper
import pl.wydarzeniawokolicy.domain.shared.StringUtils
import pl.wydarzeniawokolicy.domain.categories.api.CategoryFilter
import java.util.*

@Service
class CategoryServiceImpl(
    val categoryRepository: CategoryRepository,
    val dateTimeUtils: DateTimeUtils,
    val stringUtils: StringUtils
) :
    CategoryService {

    override fun findAll(): List<Category> = categoryRepository.findAll()
    override fun findAll(filter: CategoryFilter): List<Category> {
        return categoryRepository.findAll(filter)
    }

    override fun count(filter: CategoryFilter): Int = categoryRepository.count(filter)

    override fun create(newCategory: NewCategory): Category {
        verifySlug(null, newCategory.slug)
        val category = Category(
            name = newCategory.name,
            slug = getSlug(newCategory),
            createdAt = dateTimeUtils.getLocalDateTimeNow()
        )
        return categoryRepository.create(category)
    }

    override fun delete(slug: String) {
        categoryRepository.delete(slug)
    }

    @Transactional
    override fun update(currentSlug: String, newCategory: NewCategory): Category {
        val category = categoryRepository.findBySlug(currentSlug) ?: throw CategoryException.slugNotFound(currentSlug)
        verifySlug(category.slug, newCategory.slug)
        category.update(newCategory.name, getSlug(newCategory), dateTimeUtils.getLocalDateTimeNow())
        return categoryRepository.update(currentSlug, category)
    }

    override fun findBySlug(slug: String): Category {
        return categoryRepository.findBySlug(slug) ?: throw CategoryException.slugNotFound(slug)
    }

    private fun verifySlug(currentSlug: String?, slug: String?) {
        if (slug == null) {
            return
        }
        val categoryBySlug = categoryRepository.findBySlug(slug) ?: return
        if (currentSlug == null) {
            throw CategoryException.slugExist(slug)
        }
        if (!Objects.equals(categoryBySlug.slug, currentSlug)) {
            throw CategoryException.slugExist(slug)
        }
    }

    private fun getSlug(newCategory: NewCategory): String {
        return newCategory.slug ?: SlugHelper.generateSlug(categoryRepository, stringUtils.slug(newCategory.name))
    }

}