package pl.wydarzeniawokolicy.api.users


import jakarta.validation.Valid
import lombok.AllArgsConstructor
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.wydarzeniawokolicy.domain.users.api.UserService

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
class UserRestController(
    private val userService: UserService,
    private val userMapper: UserMapper
) {

    @GetMapping("")
    fun getAll(): List<UserDto> = userMapper.mapToDto(userService.findAll())

    @PostMapping("")
    fun create(@Valid @RequestBody signUpDto: SignUpDto): ResponseEntity<UserDto> {
        val createdUser = userService.create(userMapper.mapToDomain(signUpDto))
        return ResponseEntity(userMapper.mapToDto(createdUser), HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") userId: Long): ResponseEntity<UserDto> {
        return ResponseEntity(userMapper.mapToDto(userService.findById(userId)), HttpStatus.OK)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable("id") userId: Long, @RequestBody user: UserDetailsDto): ResponseEntity<UserDto> {
        val createdUser = userService.update(userId, userMapper.mapToDomain(user))
        return ResponseEntity(userMapper.mapToDto(createdUser), HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") userId: Long): ResponseEntity<Void> {
        userService.deleteById(userId)
        return ResponseEntity(HttpStatus.OK)
    }
}