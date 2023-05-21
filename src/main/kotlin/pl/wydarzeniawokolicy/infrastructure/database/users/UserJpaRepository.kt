package pl.wydarzeniawokolicy.infrastructure.database.users

import org.springframework.data.repository.CrudRepository

interface UserJpaRepository : CrudRepository<UserEntity, Long>