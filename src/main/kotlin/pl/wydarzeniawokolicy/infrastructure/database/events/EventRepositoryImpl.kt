package pl.wydarzeniawokolicy.infrastructure.database.events

import org.springframework.stereotype.Repository
import pl.wydarzeniawokolicy.domain.events.EventRepository
import pl.wydarzeniawokolicy.domain.events.api.Event

@Repository
class EventRepositoryImpl(val repository: EventJpaRepository) : EventRepository {

    override fun findAll(): List<Event> {
        return repository.findAll()
            .map { Event(it) }
    }

    override fun findBySlug(slug: String): Event? {
        return repository.findById(slug)
            .map { Event(it) }
            .orElse(null)
    }

    override fun save(event: Event): Event {
        val eventEntity = EventEntity(
            name = event.name,
            slug = event.slug,
            description = event.description,
            content = event.content,
            online = event.online,
            website = event.website,
            ticketWebsite = event.ticketWebsite,
            promo = event.promo,
            active = event.active,
            address = event.address?.let { AddressEmbeddable(it) },
            location = event.location?.let { GeoLocationEmbeddable(it) },
            sender = SenderEmbeddable(event.sender),
            createdAt = event.createdAt,
            updatedAt = event.updatedAt,
            deletedAt = event.deletedAt
        )
        return Event(repository.save(eventEntity))
    }

    override fun existsBySlug(slug: String): Boolean {
        return repository.existsBySlug(slug)
    }

    override fun delete(slug: String) {
        repository.deleteById(slug)
    }
}