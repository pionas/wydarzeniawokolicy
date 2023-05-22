package pl.wydarzeniawokolicy.infrastructure.database.users

import org.springframework.data.repository.CrudRepository
import java.util.*

interface UserJpaRepository : CrudRepository<UserEntity, Long> {

    fun findByName(name: String): Optional<UserEntity>
    fun findByEmail(email: String): Optional<UserEntity>
}