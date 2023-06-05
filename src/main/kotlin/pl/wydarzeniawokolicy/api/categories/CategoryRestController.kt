package pl.wydarzeniawokolicy.api.categories

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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

    @GetMapping("")
    fun getAll(): List<CategoryDto> = mapper.mapToDto(service.findAll())

    @PostMapping("")
    fun create(@Valid @RequestBody category: NewCategoryDto): ResponseEntity<CategoryDto> {
        val createdCategory = service.create(mapper.mapToDomain(category))
        return ResponseEntity(mapper.mapToDto(createdCategory), HttpStatus.CREATED)
    }

    @GetMapping("/{slug}")
    fun getById(@PathVariable("slug") slug: String): ResponseEntity<CategoryDto> {
        return ResponseEntity(mapper.mapToDto(service.findBySlug(slug)), HttpStatus.OK)
    }

    @PutMapping("/{slug}")
    fun update(
        @PathVariable("slug") slug: String,
        @Valid @RequestBody category: NewCategoryDto
    ): ResponseEntity<CategoryDto> {
        val createdRole = service.update(slug, mapper.mapToDomain(category))
        return ResponseEntity(mapper.mapToDto(createdRole), HttpStatus.OK)
    }

    @DeleteMapping("/{slug}")
    fun delete(@PathVariable("slug") slug: String): ResponseEntity<Void> {
        service.delete(slug)
        return ResponseEntity(HttpStatus.OK)
    }

}