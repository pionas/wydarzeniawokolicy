package pl.wydarzeniawokolicy.domain.users

import org.springframework.stereotype.Service
import pl.wydarzeniawokolicy.domain.users.api.*

@Service
class UserServiceImpl(val userRepository: UserRepository, val userFactory: UserFactory) : UserService {

    override fun findAll(): List<User> {
        return userRepository.findAll()
    }

    override fun create(user: UserSignUp): User {
        return userRepository.save(userFactory.create(user))
    }

    override fun update(userId: Long, userDetails: UserDetails): User {
        return userRepository.save(userFactory.update(userId, userDetails))
    }

    override fun findById(userId: Long): User {
        return userRepository.findById(userId) ?: throw UserNotFoundException(userId)
    }

    override fun deleteById(userId: Long) {
        return userRepository.deleteById(userId)
    }
}