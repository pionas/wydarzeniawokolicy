package pl.wydarzeniawokolicy.web.category

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import pl.wydarzeniawokolicy.domain.categories.api.Category
import java.time.LocalDateTime

class CategoryFormTest {

    private val categoryFormAction: CategoryFormAction = mock()

    @Test
    fun shouldFillTextFields() {
        val category = getCategory(
            "category 1",
            "category-1",
            LocalDateTime.of(2023, 5, 25, 18, 57, 0, 0),
            null
        )
        val form = CategoryForm(category, categoryFormAction)
        assertEquals(category.name, form.name.value)
        assertEquals(category.slug, form.slug.value)
    }

    @Test
    fun shouldShowEmptyTextFields() {
        val form = CategoryForm(null, categoryFormAction)
        assertEquals("", form.name.value)
        assertEquals("", form.slug.value)
    }

    private fun getCategory(
        name: String,
        slug: String,
        createdAt: LocalDateTime,
        updatedAt: LocalDateTime?,
    ) = Category(name, slug, createdAt, updatedAt, null)
}