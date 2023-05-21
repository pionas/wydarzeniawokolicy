package pl.wydarzeniawokolicy.domain.users

import org.springframework.stereotype.Service
import pl.wydarzeniawokolicy.domain.users.api.User
import pl.wydarzeniawokolicy.domain.users.api.UserService
import java.util.*

@Service
class UserServiceImpl(val userRepository: UserRepository) : UserService {

    override fun findAll(): List<User> {
        return userRepository.findAll()
    }

    override fun save(user: User): User {
        return userRepository.save(user)
    }

    override fun findById(userId: Long): Optional<User> {
        return userRepository.findById(userId)
    }

    override fun deleteById(userId: Long) {
        return userRepository.deleteById(userId)
    }
}