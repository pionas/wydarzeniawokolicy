package pl.wydarzeniawokolicy.api.categories

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import pl.wydarzeniawokolicy.domain.categories.api.CategoryService
import pl.wydarzeniawokolicy.shared.CategoryDto
import pl.wydarzeniawokolicy.shared.NewCategoryDto

@RestController
@RequestMapping("/api/v1/categories")
class CategoryRestController(
    private val service: CategoryService,
    private val mapper: CategoryMapper,
) {

    @GetMapping
    fun getAll(): List<CategoryDto> = mapper.mapToDto(service.findAll())

    @PostMapping
    @PreAuthorize("hasAnyRole('CATEGORY_MANAGEMENT','ADMIN','CATEGORY_CREATE')")
    fun create(@Valid @RequestBody category: NewCategoryDto): ResponseEntity<CategoryDto> {
        val createdCategory = service.create(mapper.mapToDomain(category))
        return ResponseEntity(mapper.mapToDto(createdCategory), HttpStatus.CREATED)
    }

    @GetMapping("/{slug}")
    fun getById(@PathVariable("slug") slug: String): ResponseEntity<CategoryDto> {
        return ResponseEntity(mapper.mapToDto(service.findBySlug(slug)), HttpStatus.OK)
    }

    @PutMapping("/{slug}")
    @PreAuthorize("hasAnyRole('CATEGORY_MANAGEMENT','ADMIN','CATEGORY_CREATE')")
    fun update(
        @PathVariable("slug") slug: String,
        @Valid @RequestBody category: NewCategoryDto
    ): ResponseEntity<CategoryDto> {
        val createdRole = service.update(slug, mapper.mapToDomain(category))
        return ResponseEntity(mapper.mapToDto(createdRole), HttpStatus.OK)
    }

    @DeleteMapping("/{slug}")
    @PreAuthorize("hasAnyRole('CATEGORY_MANAGEMENT','ADMIN','CATEGORY_DELETE')")
    fun delete(@PathVariable("slug") slug: String): ResponseEntity<Unit> {
        service.delete(slug)
        return ResponseEntity(HttpStatus.OK)
    }

}