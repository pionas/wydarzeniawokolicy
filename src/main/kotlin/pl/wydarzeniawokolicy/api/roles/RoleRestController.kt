package pl.wydarzeniawokolicy.api.roles

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.wydarzeniawokolicy.domain.roles.api.RoleService

@RestController
@RequestMapping("/api/v1/roles")
class RoleRestController(
    private val service: RoleService,
    private val mapper: RoleMapper,
) {

    @GetMapping("")
    fun getAll(): List<RoleDto> = mapper.mapToDto(service.findAll())

    @PostMapping("")
    fun create(@Valid @RequestBody role: NewRoleDto): ResponseEntity<RoleDto> {
        val createdRole = service.create(mapper.mapToDomain(role))
        return ResponseEntity(mapper.mapToDto(createdRole), HttpStatus.CREATED)
    }

    @GetMapping("/{slug}")
    fun getById(@PathVariable("slug") slug: String): ResponseEntity<RoleDto> {
        return ResponseEntity(mapper.mapToDto(service.findBySlug(slug)), HttpStatus.OK)
    }

    @PutMapping("/{slug}")
    fun update(@PathVariable("slug") slug: String, @Valid @RequestBody roleDto: NewRoleDto): ResponseEntity<RoleDto> {
        val createdRole = service.update(slug, mapper.mapToDomain(roleDto))
        return ResponseEntity(mapper.mapToDto(createdRole), HttpStatus.OK)
    }

    @DeleteMapping("/{slug}")
    fun delete(@PathVariable("slug") slug: String): ResponseEntity<Void> {
        service.delete(slug)
        return ResponseEntity(HttpStatus.OK)
    }

}