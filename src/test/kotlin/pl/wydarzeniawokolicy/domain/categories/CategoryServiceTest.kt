package pl.wydarzeniawokolicy.domain.categories

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import pl.wydarzeniawokolicy.domain.categories.api.Category
import pl.wydarzeniawokolicy.domain.categories.api.CategoryService
import pl.wydarzeniawokolicy.domain.categories.api.NewCategory
import pl.wydarzeniawokolicy.domain.shared.DateTimeUtils
import pl.wydarzeniawokolicy.domain.shared.ModelException
import pl.wydarzeniawokolicy.domain.shared.NotFoundException
import pl.wydarzeniawokolicy.domain.shared.StringUtils
import java.time.LocalDateTime

class CategoryServiceTest {

    private val dateTimeUtils: DateTimeUtils = mock()
    private val repository: CategoryRepository = mock()
    private val stringUtils: StringUtils = mock()
    private val service: CategoryService = CategoryServiceImpl(repository, dateTimeUtils, stringUtils)

    @Test
    fun shouldReturnEmptyList() {
        // given
        whenever(repository.findAll()).thenReturn(emptyList())
        // when
        val categories = service.findAll()
        // then
        Assertions.assertThat(categories).hasSize(0)
        verify(repository, times(1)).findAll()
    }

    @Test
    fun shouldReturnCategoryList() {
        // given
        whenever(repository.findAll()).thenReturn(getCategoryList())
        // when
        val categories = service.findAll()
        // then
        Assertions.assertThat(categories).hasSize(3)
        verify(repository, times(1)).findAll()
        Assertions.assertThat(categories[0])
            .hasFieldOrPropertyWithValue("name", "category 1")
            .hasFieldOrPropertyWithValue("slug", "category-1")
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 25, 18, 57, 0, 0))
        Assertions.assertThat(categories[1])
            .hasFieldOrPropertyWithValue("name", "category 2")
            .hasFieldOrPropertyWithValue("slug", "category-2")
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 25, 18, 57, 10, 0))
        Assertions.assertThat(categories[2])
            .hasFieldOrPropertyWithValue("name", "category 3")
            .hasFieldOrPropertyWithValue("slug", "category-3")
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 25, 18, 57, 20, 0))
    }

    @Test
    fun shouldCreateCategoryWithSlug() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 25, 18, 57, 0, 0)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        whenever(repository.findBySlug(any())).thenReturn(null)
        val category = NewCategory("Category 1", "category-1")
        // when
        service.create(category)
        // then
        val argumentCaptor = argumentCaptor<Category>()
        verify(repository, times(1)).create(argumentCaptor.capture())
        verify(repository, times(1)).findBySlug(any())
        Assertions.assertThat(argumentCaptor.firstValue)
            .hasFieldOrPropertyWithValue("name", "Category 1")
            .hasFieldOrPropertyWithValue("slug", "category-1")
            .hasFieldOrPropertyWithValue("createdAt", localDateTime)
    }

    @Test
    fun shouldCreateCategoryWithoutSlug() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 25, 18, 57, 0, 0)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        whenever(repository.findBySlug(any())).thenReturn(null)
        whenever(repository.existsBySlug(any())).thenReturn(false)
        whenever(stringUtils.slug(any(), any())).thenReturn("category-1")
        val category = NewCategory("Category 1", null)
        // when
        service.create(category)
        // then
        val argumentCaptor = argumentCaptor<Category>()
        verify(repository, times(1)).create(argumentCaptor.capture())
        verify(repository, times(1)).existsBySlug(any())
        Assertions.assertThat(argumentCaptor.firstValue)
            .hasFieldOrPropertyWithValue("name", "Category 1")
            .hasFieldOrPropertyWithValue("slug", "category-1")
            .hasFieldOrPropertyWithValue("createdAt", localDateTime)
    }

    @Test
    fun shouldCreateCategoryWithoutSlugAndSlugByCategoryNameExist() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 25, 18, 57, 0, 0)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        whenever(repository.findBySlug(any())).thenReturn(null)
        whenever(repository.existsBySlug("category")).thenReturn(true)
        whenever(repository.existsBySlug("category1")).thenReturn(true)
        whenever(repository.existsBySlug("category2")).thenReturn(false)
        whenever(stringUtils.slug(any(), any())).thenReturn("category")
        val category = NewCategory("Category", null)
        // when
        service.create(category)
        // then
        val argumentCaptor = argumentCaptor<Category>()
        verify(repository, times(1)).create(argumentCaptor.capture())
        verify(repository, times(3)).existsBySlug(any())
        Assertions.assertThat(argumentCaptor.firstValue)
            .hasFieldOrPropertyWithValue("name", "Category")
            .hasFieldOrPropertyWithValue("slug", "category2")
            .hasFieldOrPropertyWithValue("createdAt", localDateTime)
    }

    @Test
    fun shouldThrowCategoryBySlugExistWhenTryCreateCategoryWithSlug() {
        // given
        whenever(repository.findBySlug(any())).thenReturn(
            getCategory(
                "category 1",
                "category-1",
                LocalDateTime.of(2023, 5, 25, 18, 57, 0, 0),
                null
            )
        )
        val category = NewCategory("Category 1", "category-1")
        // when
        val exception =
            Assertions.catchThrowableOfType({ service.create(category) }, ModelException::class.java)
        // then
        Assertions.assertThat(exception.message).isEqualTo("Category by slug ${category.slug} exist")
        verify(repository, times(1)).findBySlug(category.slug!!)
    }

    @Test
    fun shouldUpdateCategoryWithSlug() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 25, 18, 57, 0, 0)
        val currentSlug = "category-slug"
        val categoryToUpdate = NewCategory("Category 1", "category-1")
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime.plusDays(1))
        whenever(repository.existsBySlug(any())).thenReturn(false)
        whenever(repository.findBySlug(currentSlug)).thenReturn(
            getCategory(
                "category 1",
                "category-slug",
                localDateTime,
                null
            )
        )
        whenever(repository.findBySlug(categoryToUpdate.slug!!)).thenReturn(null)
        // when
        service.update(currentSlug, categoryToUpdate)
        //then
        val argumentCaptor = argumentCaptor<Category>()
        verify(repository, times(1)).update(any(), argumentCaptor.capture())
        verify(repository, times(0)).existsBySlug(any())
        Assertions.assertThat(argumentCaptor.firstValue)
            .hasFieldOrPropertyWithValue("name", categoryToUpdate.name)
            .hasFieldOrPropertyWithValue("slug", categoryToUpdate.slug)
            .hasFieldOrPropertyWithValue("createdAt", localDateTime)
            .hasFieldOrPropertyWithValue("updatedAt", localDateTime.plusDays(1))
    }

    @Test
    fun shouldUpdateCategoryWithoutSlug() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 25, 18, 57, 0, 0)
        val currentSlug = "category-slug"
        val categoryToUpdate = NewCategory("Category After Update", null)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime.plusDays(1))
        whenever(repository.existsBySlug(any())).thenReturn(false)
        whenever(stringUtils.slug(any(), any())).thenReturn("category-after-update")
        whenever(repository.findBySlug(currentSlug)).thenReturn(
            getCategory(
                "category 1",
                "category-slug",
                localDateTime,
                null
            ),
            null
        )
        // when
        service.update(currentSlug, categoryToUpdate)
        //then
        val argumentCaptor = argumentCaptor<Category>()
        verify(repository, times(1)).update(any(), argumentCaptor.capture())
        verify(repository, times(1)).existsBySlug(any())
        verify(repository, times(1)).findBySlug(any())
        Assertions.assertThat(argumentCaptor.firstValue)
            .hasFieldOrPropertyWithValue("name", "Category After Update")
            .hasFieldOrPropertyWithValue("slug", "category-after-update")
            .hasFieldOrPropertyWithValue("createdAt", localDateTime)
            .hasFieldOrPropertyWithValue("updatedAt", localDateTime.plusDays(1))
    }

    @Test
    fun shouldThrowNotFoundCategoryWhenTryUpdateCategoryWithInvalidSlug() {
        // given
        whenever(repository.findBySlug(any())).thenReturn(null)
        val currentSlug = "category-slug"
        val categoryToUpdate = NewCategory("Category 1", "category-1")
        // when
        val exception =
            Assertions.catchThrowableOfType(
                { service.update(currentSlug, categoryToUpdate) },
                NotFoundException::class.java
            )
        // then
        Assertions.assertThat(exception.message).isEqualTo("Category by slug $currentSlug not exist")
        verify(repository, times(1)).findBySlug(currentSlug)
    }

    @Test
    fun shouldThrowCategoryBySlugExistWhenTryUpdateCategoryWithSlug() {
        // given
        whenever(repository.findBySlug(any())).thenReturn(
            getCategory(
                "category 1",
                "category-1",
                LocalDateTime.of(2023, 5, 25, 18, 57, 0, 0),
                null
            )
        )
        val category = NewCategory("Category 1", "category-1")
        // when
        val exception =
            Assertions.catchThrowableOfType({ service.create(category) }, ModelException::class.java)
        // then
        Assertions.assertThat(exception.message).isEqualTo("Category by slug ${category.slug} exist")
        verify(repository, times(1)).findBySlug(category.slug!!)
    }

    @Test
    fun shouldUpdateCategoryWithoutSlugButSlugFromCategoryNameExist() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 25, 18, 57, 0, 0)
        val currentSlug = "category-slug"
        val categoryToUpdate = NewCategory("Category After Update", null)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime.plusDays(1))
        whenever(repository.existsBySlug(any())).thenReturn(true, true, false)
        whenever(stringUtils.slug(any(), any())).thenReturn("category-after-update")
        whenever(repository.findBySlug(currentSlug)).thenReturn(
            getCategory(
                "category 1",
                "category-slug",
                localDateTime,
                localDateTime
            )
        )
        // when
        service.update(currentSlug, categoryToUpdate)
        //then
        val argumentCaptor = argumentCaptor<Category>()
        verify(repository, times(1)).update(any(), argumentCaptor.capture())
        verify(repository, times(3)).existsBySlug(any())
        verify(repository, times(1)).findBySlug(any())
        Assertions.assertThat(argumentCaptor.firstValue)
            .hasFieldOrPropertyWithValue("name", "Category After Update")
            .hasFieldOrPropertyWithValue("slug", "category-after-update2")
            .hasFieldOrPropertyWithValue("createdAt", localDateTime)
            .hasFieldOrPropertyWithValue("updatedAt", localDateTime.plusDays(1))
    }

    @Test
    fun shouldUpdateCategoryWithTheSameSlug() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 25, 18, 57, 0, 0)
        val currentSlug = "category-slug"
        val categoryToUpdate = NewCategory("Category After Update", currentSlug)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime.plusDays(1))
        whenever(repository.existsBySlug(any())).thenReturn(true, true, false)
        whenever(stringUtils.slug(any(), any())).thenReturn("category-after-update")
        whenever(repository.findBySlug(currentSlug)).thenReturn(
            getCategory(
                "category 1",
                currentSlug,
                localDateTime,
                localDateTime
            ),
            getCategory(
                "category 1",
                currentSlug,
                localDateTime,
                localDateTime
            )
        )
        // when
        service.update(currentSlug, categoryToUpdate)
        //then
        val argumentCaptor = argumentCaptor<Category>()
        verify(repository, times(1)).update(any(), argumentCaptor.capture())
        verify(repository, times(2)).findBySlug(any())
        Assertions.assertThat(argumentCaptor.firstValue)
            .hasFieldOrPropertyWithValue("name", "Category After Update")
            .hasFieldOrPropertyWithValue("slug", "category-slug")
            .hasFieldOrPropertyWithValue("createdAt", localDateTime)
            .hasFieldOrPropertyWithValue("updatedAt", localDateTime.plusDays(1))
    }

    @Test
    fun shouldReturnUserById() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 25, 18, 57, 0, 0)
        val category = getCategory(
            "category 1",
            "category-slug",
            localDateTime,
            localDateTime.plusDays(1)
        )
        whenever(repository.findBySlug(category.slug)).thenReturn(category)
        // when
        val categoryDetails = service.findBySlug(category.slug)
        // then
        Assertions.assertThat(categoryDetails)
            .hasFieldOrPropertyWithValue("name", category.name)
            .hasFieldOrPropertyWithValue("slug", category.slug)
            .hasFieldOrPropertyWithValue("createdAt", category.createdAt)
            .hasFieldOrPropertyWithValue("updatedAt", category.updatedAt)
        verify(repository, times(1)).findBySlug(any())
    }

    @Test
    fun shouldThrowExceptionWhenUserByIdNotExist() {
        // given
        val slug = "invalid-slug"
        whenever(repository.findBySlug(slug)).thenReturn(null)
        // when
        val exception =
            Assertions.catchThrowableOfType({ service.findBySlug(slug) }, NotFoundException::class.java)
        // then
        Assertions.assertThat(exception.message).isEqualTo("Category by slug $slug not exist")
        verify(repository, times(1)).findBySlug(slug)
    }

    @Test
    fun shouldDeleteCategory() {
        // given
        val slug = "category-1"
        // when
        service.delete(slug)
        // then
        verify(repository, times(1)).delete(slug)
    }

    private fun getCategoryList(): List<Category> {
        return listOf(
            getCategory(
                "category 1",
                "category-1",
                LocalDateTime.of(2023, 5, 25, 18, 57, 0, 0),
                null
            ),
            getCategory(
                "category 2",
                "category-2",
                LocalDateTime.of(2023, 5, 25, 18, 57, 10, 0),
                null
            ),
            getCategory(
                "category 3",
                "category-3",
                LocalDateTime.of(2023, 5, 25, 18, 57, 20, 0),
                null
            )
        )
    }

    private fun getCategory(
        name: String,
        slug: String,
        createdAt: LocalDateTime,
        updatedAt: LocalDateTime?,
    ) = Category(name, slug, createdAt, updatedAt, null)

}