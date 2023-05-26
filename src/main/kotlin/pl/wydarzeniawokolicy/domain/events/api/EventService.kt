package pl.wydarzeniawokolicy.domain.events.api

interface EventService {

    fun findAll(): List<Event>
    fun create(category: NewEvent): Event
    fun delete(slug: String)
    fun update(slug: String, eventToUpdate: NewEvent): Event
    fun findBySlug(slug: String): Event
}