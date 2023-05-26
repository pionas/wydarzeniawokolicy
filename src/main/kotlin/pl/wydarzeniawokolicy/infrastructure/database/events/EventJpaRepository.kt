package pl.wydarzeniawokolicy.infrastructure.database.events

import org.springframework.data.repository.CrudRepository

interface EventJpaRepository : CrudRepository<EventEntity, String> {

    fun existsBySlug(slug: String): Boolean
}
