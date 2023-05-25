package pl.wydarzeniawokolicy.api.categories

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpClientErrorException
import pl.wydarzeniawokolicy.api.BasicIT
import java.time.LocalDateTime


internal class CategoryRestControllerIT : BasicIT() {

    @Test
    @Disabled
    fun shouldReturnUnauthorized() {
        // given

        // when
        val result = restTemplate.getForEntity("/categories", List::class.java)

        // then
        assertNotNull(result)
        assertEquals(HttpStatus.UNAUTHORIZED, result?.statusCode)
    }

    @Test
    @Sql("/db/categories.sql")
    fun shouldReturnCategoryList() {
        // given
        // when
        val result: ResponseEntity<List<CategoryDto>> = restTemplate.exchange(
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
                { restTemplate.postForEntity("/categories", HashMap<String, Any>(), Any::class.java) },
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
                { restTemplate.postForEntity("/categories", categoryDto, Any::class.java) },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.BAD_REQUEST, result?.statusCode)
    }

    @Test
    fun shouldCreate() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 22, 11, 12, 0, 0)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        val categoryDto = NewCategoryDto("Category 1", "category-1")
        // when
        val category = restTemplate.postForEntity("/categories", categoryDto, CategoryDto::class.java)
        // then
        assertNotNull(category)
        assertEquals(HttpStatus.CREATED, category?.statusCode)
        Assertions.assertThat(category.body)
            .hasFieldOrPropertyWithValue("name", categoryDto.name)
            .hasFieldOrPropertyWithValue("slug", categoryDto.slug)
            .hasFieldOrPropertyWithValue("createdAt", localDateTime)
    }

    @Test
    @Sql("/db/categories.sql")
    fun shouldReturnCategoryDetailsById() {
        // given
        // when
        val result = restTemplate.getForEntity("/categories/category-2", CategoryDto::class.java)
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
                { restTemplate.getForEntity("/categories/100", Object::class.java) },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.NOT_FOUND, result?.statusCode)
    }

    @Test
    fun shouldUpdateCategoryDetails() {
    }

    @Test
    fun shouldReturnNotFoundWhenTryUpdateCategoryDetailsButCategoryNotExist() {
    }

    @Test
    fun shouldReturnNotFoundWhenTryUpdateCategoryDetails() {
        // null, empty invalid name, email, password, passwordConfirm
    }

    @Test
    fun shouldDelete() {
        // given
        // when
        val result = restTemplate.exchange("/categories/100", HttpMethod.DELETE, null, Any::class.java)
        // then
        assertEquals(HttpStatus.OK, result.statusCode)
    }
}