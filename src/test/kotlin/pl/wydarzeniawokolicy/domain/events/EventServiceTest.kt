package pl.wydarzeniawokolicy.domain.events

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import pl.wydarzeniawokolicy.domain.events.api.*
import pl.wydarzeniawokolicy.domain.shared.DateTimeUtils
import pl.wydarzeniawokolicy.domain.shared.ModelException
import pl.wydarzeniawokolicy.domain.shared.NotFoundException
import pl.wydarzeniawokolicy.domain.shared.StringUtils
import java.time.LocalDateTime

class EventServiceTest {

    private val dateTimeUtils: DateTimeUtils = mock()
    private val repository: EventRepository = mock()
    private val stringUtils: StringUtils = mock()
    private val service: EventService = EventServiceImpl(repository, dateTimeUtils, stringUtils)

    @Test
    fun shouldReturnEmptyList() {
        // given
        whenever(repository.findAll()).thenReturn(emptyList())
        // when
        val events = service.findAll()
        // then
        Assertions.assertThat(events).hasSize(0)
        verify(repository, times(1)).findAll()
    }

    @Test
    fun shouldReturnEventList() {
        // given
        whenever(repository.findAll()).thenReturn(getEventList())
        // when
        val events = service.findAll()
        // then
        Assertions.assertThat(events).hasSize(3)
        verify(repository, times(1)).findAll()
        Assertions.assertThat(events[0])
            .hasFieldOrPropertyWithValue("name", "event 1")
            .hasFieldOrPropertyWithValue("slug", "event-1")
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 25, 18, 57, 0, 0))
        Assertions.assertThat(events[1])
            .hasFieldOrPropertyWithValue("name", "event 2")
            .hasFieldOrPropertyWithValue("slug", "event-2")
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 25, 18, 57, 10, 0))
        Assertions.assertThat(events[2])
            .hasFieldOrPropertyWithValue("name", "event 3")
            .hasFieldOrPropertyWithValue("slug", "event-3")
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 25, 18, 57, 20, 0))
    }

    @Test
    fun shouldCreateEventWithSlug() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 25, 18, 57, 0, 0)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        whenever(repository.findBySlug(any())).thenReturn(null)
        val event = NewEvent(
            name = "Event 3",
            slug = "event-3",
            sender = Sender(null, null, 1L)
        )
        // when
        service.create(event)
        // then
        val argumentCaptor = argumentCaptor<Event>()
        verify(repository, times(1)).save(argumentCaptor.capture())
        verify(repository, times(1)).findBySlug(any())
        Assertions.assertThat(argumentCaptor.firstValue)
            .hasFieldOrPropertyWithValue("name", "Event 3")
            .hasFieldOrPropertyWithValue("slug", "event-3")
            .hasFieldOrPropertyWithValue("createdAt", localDateTime)
    }

    @Test
    fun shouldCreateEventWithoutSlug() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 25, 18, 57, 0, 0)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        whenever(repository.findBySlug(any())).thenReturn(null)
        whenever(repository.existsBySlug(any())).thenReturn(false)
        whenever(stringUtils.slug(any(), any())).thenReturn("event-3")
        val event = NewEvent(name = "Event 3", sender = Sender(null, null, 1L))
        // when
        service.create(event)
        // then
        val argumentCaptor = argumentCaptor<Event>()
        verify(repository, times(1)).save(argumentCaptor.capture())
        verify(repository, times(1)).existsBySlug(any())
        Assertions.assertThat(argumentCaptor.firstValue)
            .hasFieldOrPropertyWithValue("name", "Event 3")
            .hasFieldOrPropertyWithValue("slug", "event-3")
            .hasFieldOrPropertyWithValue("createdAt", localDateTime)
    }

    @Test
    fun shouldCreateEventWithoutSlugAndSlugByEventNameExist() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 25, 18, 57, 0, 0)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        whenever(repository.findBySlug(any())).thenReturn(null)
        whenever(repository.existsBySlug("event")).thenReturn(true)
        whenever(repository.existsBySlug("event1")).thenReturn(true)
        whenever(repository.existsBySlug("event2")).thenReturn(false)
        whenever(stringUtils.slug(any(), any())).thenReturn("event")
        val event = NewEvent(name = "Event", sender = Sender(null, null, 1L))
        // when
        service.create(event)
        // then
        val argumentCaptor = argumentCaptor<Event>()
        verify(repository, times(1)).save(argumentCaptor.capture())
        verify(repository, times(3)).existsBySlug(any())
        Assertions.assertThat(argumentCaptor.firstValue)
            .hasFieldOrPropertyWithValue("name", "Event")
            .hasFieldOrPropertyWithValue("slug", "event2")
            .hasFieldOrPropertyWithValue("createdAt", localDateTime)
    }

    @Test
    fun shouldThrowEventBySlugExistWhenTryCreateEventWithSlug() {
        // given
        whenever(repository.findBySlug(any())).thenReturn(
            getEvent(
                name = "event 1",
                slug = "event-1",
                createdAt = LocalDateTime.of(2023, 5, 25, 18, 57, 0, 0),
                updatedAt = null,
                sender = Sender(null, null, 1L)
            )
        )
        val event = NewEvent(name = "Event 1", slug = "event-1", sender = Sender(null, null, 1L))
        // when
        val exception =
            Assertions.catchThrowableOfType({ service.create(event) }, ModelException::class.java)
        // then
        Assertions.assertThat(exception.message).isEqualTo("Event by slug ${event.slug} exist")
        verify(repository, times(1)).findBySlug(event.slug!!)
    }

    @Test
    fun shouldUpdateEventWithSlug() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 25, 18, 57, 0, 0)
        val currentSlug = "event-slug"
        val eventToUpdate = NewEvent(name = "Event 1", slug = "event-1", sender = Sender(null, null, 1L))
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime.plusDays(1))
        whenever(repository.existsBySlug(any())).thenReturn(false)
        whenever(repository.findBySlug(currentSlug)).thenReturn(
            getEvent(
                name = "event 1",
                slug = "event-slug",
                createdAt = localDateTime,
                updatedAt = null,
                sender = Sender(null, null, 1L)
            )
        )
        whenever(repository.findBySlug(eventToUpdate.slug!!)).thenReturn(null)
        // when
        service.update(currentSlug, eventToUpdate)
        //then
        val argumentCaptor = argumentCaptor<Event>()
        verify(repository, times(1)).save(argumentCaptor.capture())
        verify(repository, times(0)).existsBySlug(any())
        Assertions.assertThat(argumentCaptor.firstValue)
            .hasFieldOrPropertyWithValue("name", eventToUpdate.name)
            .hasFieldOrPropertyWithValue("slug", eventToUpdate.slug)
            .hasFieldOrPropertyWithValue("createdAt", localDateTime)
            .hasFieldOrPropertyWithValue("updatedAt", localDateTime.plusDays(1))
    }

    @Test
    fun shouldUpdateEventWithoutSlug() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 25, 18, 57, 0, 0)
        val currentSlug = "event-slug"
        val eventToUpdate = NewEvent(name = "Event After Update", sender = Sender(null, null, 1L))
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime.plusDays(1))
        whenever(repository.existsBySlug(any())).thenReturn(false)
        whenever(stringUtils.slug(any(), any())).thenReturn("event-after-update")
        whenever(repository.findBySlug(currentSlug)).thenReturn(
            getEvent(
                name = "event 1",
                slug = "event-slug",
                createdAt = localDateTime,
                updatedAt = null,
                sender = Sender(null, null, 1L)
            ),
            null
        )
        // when
        service.update(currentSlug, eventToUpdate)
        //then
        val argumentCaptor = argumentCaptor<Event>()
        verify(repository, times(1)).save(argumentCaptor.capture())
        verify(repository, times(1)).existsBySlug(any())
        verify(repository, times(1)).findBySlug(any())
        Assertions.assertThat(argumentCaptor.firstValue)
            .hasFieldOrPropertyWithValue("name", "Event After Update")
            .hasFieldOrPropertyWithValue("slug", "event-after-update")
            .hasFieldOrPropertyWithValue("createdAt", localDateTime)
            .hasFieldOrPropertyWithValue("updatedAt", localDateTime.plusDays(1))
    }

    @Test
    fun shouldThrowNotFoundEventWhenTryUpdateEventWithInvalidSlug() {
        // given
        whenever(repository.findBySlug(any())).thenReturn(null)
        val currentSlug = "event-slug"
        val eventToUpdate = NewEvent(name = "Event 1", slug = "event-1", sender = Sender(null, null, 1L))
        // when
        val exception =
            Assertions.catchThrowableOfType(
                { service.update(currentSlug, eventToUpdate) },
                NotFoundException::class.java
            )
        // then
        Assertions.assertThat(exception.message).isEqualTo("Event by slug $currentSlug not exist")
        verify(repository, times(1)).findBySlug(currentSlug)
    }

    @Test
    fun shouldThrowEventBySlugExistWhenTryUpdateEventWithSlug() {
        // given
        whenever(repository.findBySlug(any())).thenReturn(
            getEvent(
                name = "event 1",
                slug = "event-1",
                createdAt = LocalDateTime.of(2023, 5, 25, 18, 57, 0, 0),
                updatedAt = null,
                sender = Sender(null, null, 1L)
            )
        )
        val event = NewEvent(name = "Event 1", slug = "event-1", sender = Sender(null, null, 1L))
        // when
        val exception =
            Assertions.catchThrowableOfType({ service.create(event) }, ModelException::class.java)
        // then
        Assertions.assertThat(exception.message).isEqualTo("Event by slug ${event.slug} exist")
        verify(repository, times(1)).findBySlug(event.slug!!)
    }

    @Test
    fun shouldUpdateEventWithoutSlugButSlugFromEventNameExist() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 25, 18, 57, 0, 0)
        val currentSlug = "event-slug"
        val eventToUpdate = NewEvent(name = "Event After Update", sender = Sender(null, null, 1L))
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime.plusDays(1))
        whenever(repository.existsBySlug(any())).thenReturn(true, true, false)
        whenever(stringUtils.slug(any(), any())).thenReturn("event-after-update")
        whenever(repository.findBySlug(currentSlug)).thenReturn(
            getEvent(
                name = "event 1",
                slug = "event-slug",
                createdAt = localDateTime,
                updatedAt = localDateTime,
                sender = Sender(null, null, 1L)
            )
        )
        // when
        service.update(currentSlug, eventToUpdate)
        //then
        val argumentCaptor = argumentCaptor<Event>()
        verify(repository, times(1)).save(argumentCaptor.capture())
        verify(repository, times(3)).existsBySlug(any())
        verify(repository, times(1)).findBySlug(any())
        Assertions.assertThat(argumentCaptor.firstValue)
            .hasFieldOrPropertyWithValue("name", "Event After Update")
            .hasFieldOrPropertyWithValue("slug", "event-after-update2")
            .hasFieldOrPropertyWithValue("createdAt", localDateTime)
            .hasFieldOrPropertyWithValue("updatedAt", localDateTime.plusDays(1))
    }

    @Test
    fun shouldReturnUserById() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 25, 18, 57, 0, 0)
        val event = getEvent(
            name = "event 1",
            slug = "event-slug",
            createdAt = localDateTime,
            updatedAt = localDateTime.plusDays(1),
            sender = Sender(null, null, 1L)
        )
        whenever(repository.findBySlug(event.slug)).thenReturn(event)
        // when
        val eventDetails = service.findBySlug(event.slug)
        // then
        Assertions.assertThat(eventDetails)
            .hasFieldOrPropertyWithValue("name", event.name)
            .hasFieldOrPropertyWithValue("slug", event.slug)
            .hasFieldOrPropertyWithValue("createdAt", event.createdAt)
            .hasFieldOrPropertyWithValue("updatedAt", event.updatedAt)
        verify(repository, times(1)).findBySlug(any())
    }

    @Test
    fun shouldThrowExceptionWhenUserByIdNotExist() {
        // given
        val slug = "invalid-slug"
        whenever(repository.findBySlug(slug)).thenReturn(null)
        // when
        val exception =
            Assertions.catchThrowableOfType({ service.findBySlug(slug) }, NotFoundException::class.java)
        // then
        Assertions.assertThat(exception.message).isEqualTo("Event by slug $slug not exist")
        verify(repository, times(1)).findBySlug(slug)
    }

    @Test
    fun shouldDeleteEvent() {
        // given
        val slug = "event-1"
        // when
        service.delete(slug)
        // then
        verify(repository, times(1)).delete(slug)
    }

    private fun getEventList(): List<Event> {
        return listOf(
            getEvent(
                name = "event 1",
                slug = "event-1",
                createdAt = LocalDateTime.of(2023, 5, 25, 18, 57, 0, 0),
                updatedAt = null,
                sender = Sender("Sender 1", "Sender1@example.com", null)
            ),
            getEvent(
                name = "event 2",
                slug = "event-2",
                createdAt = LocalDateTime.of(2023, 5, 25, 18, 57, 10, 0),
                updatedAt = null,
                sender = Sender("Sender 2", "Sender2@example.com", null)
            ),
            getEvent(
                name = "event 3",
                slug = "event-3",
                createdAt = LocalDateTime.of(2023, 5, 25, 18, 57, 20, 0),
                updatedAt = null,
                sender = Sender(null, null, 1L)
            )
        )
    }

    private fun getEvent(
        name: String,
        slug: String,
        description: String = "",
        content: String = "",
        online: Boolean = false,
        website: String? = "",
        ticketWebsite: String? = null,
        promo: Boolean = false,
        active: Boolean = false,
        address: Address? = null,
        location: GeoLocation? = null,
        sender: Sender,
        createdAt: LocalDateTime,
        updatedAt: LocalDateTime?
    ) = Event(
        name = name,
        slug = slug,
        description = description,
        content = content,
        online = online,
        website = website,
        ticketWebsite = ticketWebsite,
        promo = promo,
        active = active,
        address = address,
        location = location,
        sender = sender,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

}