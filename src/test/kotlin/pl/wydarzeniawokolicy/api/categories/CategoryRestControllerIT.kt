package pl.wydarzeniawokolicy.api.categories

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.whenever
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpClientErrorException
import pl.wydarzeniawokolicy.api.BasicIT
import pl.wydarzeniawokolicy.infrastructure.database.categories.CategoryEntity
import pl.wydarzeniawokolicy.shared.CategoryDto
import pl.wydarzeniawokolicy.shared.NewCategoryDto
import java.time.LocalDateTime
import java.util.stream.Stream

internal class CategoryRestControllerIT : BasicIT() {

    @Test
    @Disabled
    fun shouldReturnUnauthorized() {
        // given

        // when
        val result = restApiTemplate.getForEntity("/categories", List::class.java)

        // then
        assertNotNull(result)
        assertEquals(HttpStatus.UNAUTHORIZED, result?.statusCode)
    }

    @Test
    @Sql("/db/categories.sql")
    fun shouldReturnCategoryList() {
        // given
        // when
        val result: ResponseEntity<List<CategoryDto>> = restApiTemplate.exchange(
            "/categories",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<List<CategoryDto>>() {})
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.OK, result.statusCode)
        val categories = result.body!!
        Assertions.assertThat(categories).hasSize(3)
        Assertions.assertThat(categories[0])
            .hasFieldOrPropertyWithValue("name", "Category 1")
            .hasFieldOrPropertyWithValue("slug", "category-1")
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 25, 18, 57, 0, 0))
        Assertions.assertThat(categories[1])
            .hasFieldOrPropertyWithValue("name", "Category 2")
            .hasFieldOrPropertyWithValue("slug", "category-2")
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 25, 18, 57, 10, 0))
        Assertions.assertThat(categories[2])
            .hasFieldOrPropertyWithValue("name", "Category 3")
            .hasFieldOrPropertyWithValue("slug", "category-3")
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 25, 18, 57, 20, 0))
    }

    @Test
    fun shouldReturnInternalServerErrorWhenTryCreate() {
        // given
        // when
        val result =
            Assertions.catchThrowableOfType(
                { restApiTemplate.postForEntity("/categories", HashMap<String, Any>(), Any::class.java) },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.BAD_REQUEST, result?.statusCode)
    }

    @Test
    fun shouldReturnBadRequestWhenTryCreate() {
        // given
        val categoryDto = NewCategoryDto("", null)
        // when
        val result =
            Assertions.catchThrowableOfType(
                { restApiTemplate.postForEntity("/categories", categoryDto, Any::class.java) },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.BAD_REQUEST, result?.statusCode)
    }

    @Test
    @Sql("/db/categories.sql")
    fun shouldCreate() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 22, 11, 12, 0, 0)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        val categoryDto = NewCategoryDto("Category 1", null)
        // when
        val category = restApiTemplate.postForEntity("/categories", categoryDto, CategoryDto::class.java)
        // then
        assertNotNull(category)
        assertEquals(HttpStatus.CREATED, category?.statusCode)
        Assertions.assertThat(category.body)
            .hasFieldOrPropertyWithValue("name", categoryDto.name)
            .hasFieldOrPropertyWithValue("slug", "category-11")
            .hasFieldOrPropertyWithValue("createdAt", localDateTime)
    }

    @Test
    @Sql("/db/categories.sql")
    fun shouldReturnCategoryDetailsById() {
        // given
        // when
        val result = restApiTemplate.getForEntity("/categories/category-2", CategoryDto::class.java)
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.OK, result?.statusCode)
        Assertions.assertThat(result.body!!)
            .hasFieldOrPropertyWithValue("name", "Category 2")
            .hasFieldOrPropertyWithValue("slug", "category-2")
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 25, 18, 57, 10, 0))
            .hasFieldOrPropertyWithValue("updatedAt", LocalDateTime.of(2023, 5, 25, 19, 45, 10, 0))
    }

    @Test
    fun shouldReturnNotFoundWhenTryGetCategoryDetailsById() {
        // given
        // when
        val result =
            Assertions.catchThrowableOfType(
                { restApiTemplate.getForEntity("/categories/100", Object::class.java) },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.NOT_FOUND, result?.statusCode)
    }

    @Test
    fun shouldReturnInternalServerErrorWhenTryUpdate() {
        // given
        // when
        val result =
            Assertions.catchThrowableOfType(
                { restApiTemplate.put("/categories/category-1", HashMap<String, Any>(), Any::class.java) },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.BAD_REQUEST, result?.statusCode)
    }

    @Test
    fun shouldReturnNotFoundWhenTryUpdateByCategoryNotExist() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 22, 11, 12, 0, 0)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        val categoryDto = NewCategoryDto("Category 1", "category-1")
        // when
        val requestEntity = HttpEntity(categoryDto)
        val result =
            Assertions.catchThrowableOfType(
                {
                    restApiTemplate.exchange(
                        "/categories/category-1",
                        HttpMethod.PUT,
                        requestEntity,
                        CategoryDto::class.java
                    )
                },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.NOT_FOUND, result?.statusCode)
    }

    @Test
    @Sql("/db/categories.sql")
    fun shouldReturnBadRequestWhenTryUpdateButCategorySlugExist() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 22, 11, 12, 0, 0)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        val categoryDto = NewCategoryDto("Category 2", "category-2")
        // when
        val requestEntity = HttpEntity(categoryDto)
        val result =
            Assertions.catchThrowableOfType(
                {
                    restApiTemplate.exchange(
                        "/categories/category-1",
                        HttpMethod.PUT,
                        requestEntity,
                        CategoryDto::class.java
                    )
                },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.BAD_REQUEST, result?.statusCode)
    }

    @ParameterizedTest
    @MethodSource("provideCategoryDtoList")
    fun shouldReturnBadRequestWhenTryUpdate(categoryName: String?, categorySlug: String?, message: String) {
        // given
        val categoryDto = NewCategoryDto(categoryName, categorySlug)
        // when
        val result =
            Assertions.catchThrowableOfType(
                { restApiTemplate.put("/categories/category-1", categoryDto, Any::class.java) },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.BAD_REQUEST, result!!.statusCode)
        assertTrue(result.message!!.contains(message))
    }

    @ParameterizedTest
    @MethodSource("provideCategoryDtoValidList")
    @Sql("/db/categories.sql")
    fun shouldUpdate(categoryName: String, categorySlug: String?, expectedCategorySlug: String) {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 25, 19, 57, 0, 0)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        val categoryDto = NewCategoryDto(categoryName, categorySlug)
        // when
        val requestEntity = HttpEntity(categoryDto)
        val category: ResponseEntity<CategoryDto> =
            restApiTemplate.exchange("/categories/category-1", HttpMethod.PUT, requestEntity, CategoryDto::class.java)
        // then
        assertNotNull(category)
        assertEquals(HttpStatus.OK, category.statusCode)
        Assertions.assertThat(category.body)
            .hasFieldOrPropertyWithValue("name", categoryName)
            .hasFieldOrPropertyWithValue("slug", expectedCategorySlug)
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 25, 18, 57, 0, 0))
            .hasFieldOrPropertyWithValue("updatedAt", localDateTime)
    }

    @Test
    @Sql("/db/categories.sql")
    fun shouldDelete() {
        // given
        // when
        val result = restApiTemplate.exchange("/categories/category-1", HttpMethod.DELETE, null, Any::class.java)
        // then
        assertEquals(HttpStatus.OK, result.statusCode)
        val categoryEntity = dbUtils.em().find(CategoryEntity::class.java, "category-1")
        assertNull(categoryEntity)
    }

    companion object {
        @JvmStatic
        private fun provideCategoryDtoList(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(null, null, "newCategoryDto.name"),
                Arguments.of("Na", null, "newCategoryDto.name"),
                Arguments.of("Category", "ro", "newCategoryDto.slug"),
                Arguments.of("", null, "newCategoryDto.name"),
                Arguments.of(null, "", "newCategoryDto.name"),
                Arguments.of("", "", "newCategoryDto.name"),
                Arguments.of(
                    "VeryLongCategoryNameVeryLongCategoryNameVeryLongCategoryNameVeryLongCategoryNameVeryLongCategoryNameVeryLongCategoryName",
                    null,
                    "newCategoryDto.name"
                ),
                Arguments.of(
                    "Category Ok",
                    "VeryLongCategoryNameVeryLongCategoryNameVeryLongCategoryNameVeryLongCategoryNameVeryLongCategoryNameVeryLongCategoryName",
                    "newCategoryDto.slug"
                )
            )
        }

        @JvmStatic
        private fun provideCategoryDtoValidList(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("NewCategoryName", null, "newcategoryname"),
                Arguments.of("NewCategoryName", "new-category-name", "new-category-name"),
                Arguments.of("Category 2", null, "category-21"),
            )
        }
    }
}