package pl.wydarzeniawokolicy.api.users


import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import pl.wydarzeniawokolicy.domain.users.api.UserService

@RestController
@RequestMapping("/api/v1/users")
class UserRestController(
    private val userService: UserService,
    private val userMapper: UserMapper
) {

    @GetMapping
    fun getAll(): List<UserDto> = userMapper.mapToDto(userService.findAll())

    @PostMapping
    fun create(@Valid @RequestBody signUpDto: SignUpDto): ResponseEntity<UserDto> {
        val createdUser = userService.create(userMapper.mapToDomain(signUpDto))
        return ResponseEntity(userMapper.mapToDto(createdUser), HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") userId: Long): ResponseEntity<UserDto> {
        return ResponseEntity(userMapper.mapToDto(userService.findById(userId)), HttpStatus.OK)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER_MANAGEMENT','ADMIN','USER_CREATE')")
    fun update(
        @PathVariable("id") userId: Long,
        @Valid @RequestBody userDetailsDto: UserDetailsDto
    ): ResponseEntity<UserDto> {
        val createdUser = userService.update(userId, userMapper.mapToDomain(userDetailsDto))
        return ResponseEntity(userMapper.mapToDto(createdUser), HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER_MANAGEMENT','ADMIN','USER_DELETE')")
    fun delete(@PathVariable("id") userId: Long): ResponseEntity<Unit> {
        userService.deleteById(userId)
        return ResponseEntity(HttpStatus.OK)
    }
}