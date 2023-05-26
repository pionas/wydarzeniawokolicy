package pl.wydarzeniawokolicy.domain.events

import pl.wydarzeniawokolicy.domain.events.api.Event
import pl.wydarzeniawokolicy.domain.shared.SlugRepository

interface EventRepository : SlugRepository {

    fun findAll(): List<Event>
    fun save(event: Event): Event
    fun delete(slug: String)
    fun findBySlug(slug: String): Event?

}
