package pl.wydarzeniawokolicy.domain.events

import org.springframework.stereotype.Service
import pl.wydarzeniawokolicy.domain.events.api.Event
import pl.wydarzeniawokolicy.domain.events.api.EventException
import pl.wydarzeniawokolicy.domain.events.api.EventService
import pl.wydarzeniawokolicy.domain.events.api.NewEvent
import pl.wydarzeniawokolicy.domain.shared.DateTimeUtils
import pl.wydarzeniawokolicy.domain.shared.SlugHelper
import pl.wydarzeniawokolicy.domain.shared.StringUtils
import java.util.*

@Service
class EventServiceImpl(
    val repository: EventRepository,
    val dateTimeUtils: DateTimeUtils,
    val stringUtils: StringUtils
) : EventService {

    override fun findAll(): List<Event> = repository.findAll()

    override fun create(newEvent: NewEvent): Event {
        verifySlug(null, newEvent.slug)
        val event = Event(
            name = newEvent.name,
            slug = getSlug(newEvent),
            description = newEvent.description,
            content = newEvent.content,
            online = newEvent.online,
            website = newEvent.website,
            ticketWebsite = newEvent.ticketWebsite,
            promo = newEvent.promo,
            active = newEvent.active,
            address = newEvent.address,
            location = newEvent.location,
            sender = newEvent.sender,
            createdAt = dateTimeUtils.getLocalDateTimeNow()
        )
        return repository.save(event)
    }

    override fun delete(slug: String) {
        repository.delete(slug)
    }

    override fun update(slug: String, eventToUpdate: NewEvent): Event {
        val event = repository.findBySlug(slug) ?: throw EventException.eventNotFound(slug)
        verifySlug(event.slug, eventToUpdate.slug)
        event.apply {
            this.name = eventToUpdate.name
            this.slug = getSlug(eventToUpdate)
            this.description = eventToUpdate.description
            this.content = eventToUpdate.content
            this.online = eventToUpdate.online
            this.website = eventToUpdate.website
            this.ticketWebsite = eventToUpdate.ticketWebsite
            this.promo = eventToUpdate.promo
            this.active = eventToUpdate.active
            this.address = eventToUpdate.address
            this.location = eventToUpdate.location
            this.sender = eventToUpdate.sender
            this.updatedAt = dateTimeUtils.getLocalDateTimeNow()
        }
        return repository.save(event)
    }

    override fun findBySlug(slug: String): Event {
        return repository.findBySlug(slug) ?: throw EventException.eventNotFound(slug)
    }

    private fun getSlug(newEvent: NewEvent): String {
        return newEvent.slug ?: SlugHelper.generateSlug(repository, stringUtils.slug(newEvent.name))
    }

    private fun verifySlug(currentSlug: String?, slug: String?) {
        if (slug == null) {
            return
        }
        val eventBySlug = repository.findBySlug(slug) ?: return
        if (currentSlug == null) {
            throw EventException.slugExist(slug)
        }
        if (!Objects.equals(eventBySlug.slug, currentSlug)) {
            throw EventException.slugExist(currentSlug)
        }
    }
}