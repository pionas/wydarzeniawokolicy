package pl.wydarzeniawokolicy.api.roles

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import pl.wydarzeniawokolicy.domain.roles.api.RoleService

@RestController
@RequestMapping("/api/v1/roles")
class RoleRestController(
    private val service: RoleService,
    private val mapper: RoleMapper,
) {

    @GetMapping
    fun getAll(): List<RoleDto> = mapper.mapToDto(service.findAll())

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_MANAGEMENT','ADMIN')")
    fun create(@Valid @RequestBody role: NewRoleDto): ResponseEntity<RoleDto> {
        val createdRole = service.create(mapper.mapToDomain(role))
        return ResponseEntity(mapper.mapToDto(createdRole), HttpStatus.CREATED)
    }

    @GetMapping("/{slug}")
    fun getById(@PathVariable("slug") slug: String): ResponseEntity<RoleDto> {
        return ResponseEntity(mapper.mapToDto(service.findBySlug(slug)), HttpStatus.OK)
    }

    @PutMapping("/{slug}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGEMENT','ADMIN')")
    fun update(@PathVariable("slug") slug: String, @Valid @RequestBody roleDto: NewRoleDto): ResponseEntity<RoleDto> {
        val createdRole = service.update(slug, mapper.mapToDomain(roleDto))
        return ResponseEntity(mapper.mapToDto(createdRole), HttpStatus.OK)
    }

    @DeleteMapping("/{slug}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGEMENT','ADMIN')")
    fun delete(@PathVariable("slug") slug: String): ResponseEntity<Void> {
        service.delete(slug)
        return ResponseEntity(HttpStatus.OK)
    }

}