package pl.wydarzeniawokolicy.api.users


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.wydarzeniawokolicy.domain.users.api.User
import pl.wydarzeniawokolicy.domain.users.api.UserService

@RestController
@RequestMapping("/api/users")
class UserRestController(@Autowired private val userService: UserService) {

    @GetMapping("")
    fun getAllUsers(): List<User> = userService.findAll()

    @PostMapping("")
    fun createUser(@RequestBody user: User): ResponseEntity<User> {
        val createdUser = userService.save(user)
        return ResponseEntity(createdUser, HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable("id") userId: Long): ResponseEntity<User> {
        return userService.findById(userId)
            .map { ResponseEntity(it, HttpStatus.OK) }
            .orElseGet { ResponseEntity(HttpStatus.NOT_FOUND) }
    }

    @PutMapping("/{id}")
    fun updateUserById(@PathVariable("id") userId: Long, @RequestBody user: User): ResponseEntity<User> {
        return userService.findById(userId)
            .map { userService.save(it.update(user)) }
            .map { ResponseEntity(it, HttpStatus.OK) }
            .orElseGet { ResponseEntity(HttpStatus.NOT_FOUND) }
    }

    @DeleteMapping("/{id}")
    fun deleteUserById(@PathVariable("id") userId: Long): ResponseEntity<User> {
        return userService.findById(userId)
            .map {
                userService.deleteById(it.id)
                ResponseEntity(it, HttpStatus.OK)
            }
            .orElseGet { ResponseEntity(HttpStatus.NOT_FOUND) }
    }
}