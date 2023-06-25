package pl.wydarzeniawokolicy.api.events

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.whenever
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.web.client.HttpClientErrorException
import pl.wydarzeniawokolicy.BasicIT
import pl.wydarzeniawokolicy.infrastructure.database.events.EventEntity
import java.time.LocalDateTime
import java.util.stream.Stream

internal class EventRestControllerIT : BasicIT() {

    private val EVENTS_URI: String = "/events"
    private val EVENTS_DETAILS_URI: String = "/events/%s"

    @Test
    @Sql(scripts = ["/db/events.sql"], config = SqlConfig(encoding = "UTF-8"))
    fun shouldReturnEventList() {
        // given
        // when
        val result: ResponseEntity<List<EventDto>> = restApiTemplate.exchange(
            EVENTS_URI,
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<List<EventDto>>() {})
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.OK, result.statusCode)
        val events = result.body!!
        Assertions.assertThat(events).hasSize(3)
        Assertions.assertThat(events[0])
            .hasFieldOrPropertyWithValue("name", "Pająki i skorpiony")
            .hasFieldOrPropertyWithValue("slug", "pajaki-i-skorpiony")
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 27, 7, 55, 0, 0))
        Assertions.assertThat(events[1])
            .hasFieldOrPropertyWithValue("name", "Dzień Dziecka w parku Szczęśliwickim")
            .hasFieldOrPropertyWithValue("slug", "dzien-dziecka-w-parku-szczesliwickim")
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 27, 7, 55, 10, 0))
        Assertions.assertThat(events[2])
            .hasFieldOrPropertyWithValue("name", "Kopernik i jego świat")
            .hasFieldOrPropertyWithValue("slug", "kopernik-i-jego-swiat")
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 27, 7, 55, 20, 0))
    }

    @Test
    fun shouldReturnUnauthorizedWhenUserNotExistAndTryUploadFile() {
        // given
        val eventDto = getEventDto("Event 1", "event-1")
        // when
        val result =
            Assertions.catchThrowableOfType(
                { authorizedRestTemplate.postForEntity(EVENTS_URI, eventDto, Any::class.java) },
                HttpClientErrorException::class.java
            )

        // then
        assertNotNull(result)
        assertEquals(HttpStatus.UNAUTHORIZED, result?.statusCode)
    }

    @Test
    fun shouldReturnWrongPasswordWhenTryCreateCategory() {
        // given
        val eventDto = getEventDto("Event 1", "event-1")
        // when
        val result =
            Assertions.catchThrowableOfType(
                { wrongPasswordRestTemplate.postForEntity(EVENTS_URI, eventDto, Any::class.java) },
                HttpClientErrorException::class.java
            )

        // then
        assertNotNull(result)
        assertEquals(HttpStatus.UNAUTHORIZED, result?.statusCode)
    }

    @Test
    fun shouldReturnInternalServerErrorWhenTryCreate() {
        // given
        // when
        val result =
            Assertions.catchThrowableOfType(
                { restApiTemplate.postForEntity(EVENTS_URI, HashMap<String, Any>(), Any::class.java) },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.BAD_REQUEST, result?.statusCode)
    }

    @Test
    @Sql(scripts = ["/db/users.sql", "/db/roles.sql", "/db/users_roles.sql"], config = SqlConfig(encoding = "UTF-8"))
    fun shouldReturnBadRequestWhenTryCreate() {
        // given
        val eventDto = NewEventDto("", null)
        // when
        val result =
            Assertions.catchThrowableOfType(
                { authorizedRestTemplate.postForEntity(EVENTS_URI, eventDto, Any::class.java) },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.BAD_REQUEST, result?.statusCode)
    }

    @Test
    @Sql(
        scripts = ["/db/events.sql", "/db/users.sql", "/db/roles.sql", "/db/users_roles.sql"],
        config = SqlConfig(encoding = "UTF-8")
    )
    fun shouldCreateByAdmin() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 22, 11, 12, 0, 0)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        val eventDto = getEventDto(
            name = "Event 1",
            slug = "event-1",
            description = "description",
            content = "content",
            online = true,
            website = "http://localhost",
            ticketWebsite = "https://ticket.localhost",
            promo = true,
            address = AddressDto("Country", "Voivodeship", "City", "Street", "Apartment"),
            active = true,
            location = GeoLocationDto(52.231903F, 21.006016F),
        )
        // when
        val event = authorizedRestTemplate.postForEntity(EVENTS_URI, eventDto, EventDto::class.java)
        // then
        assertNotNull(event)
        assertEquals(HttpStatus.CREATED, event?.statusCode)
        Assertions.assertThat(event.body)
            .hasFieldOrPropertyWithValue("name", eventDto.name)
            .hasFieldOrPropertyWithValue("slug", eventDto.slug)
            .hasFieldOrPropertyWithValue("sender.name", null)
            .hasFieldOrPropertyWithValue("sender.email", null)
            .hasFieldOrPropertyWithValue("sender.userId", 1L)
            .hasFieldOrPropertyWithValue("createdAt", localDateTime)
    }

    @Test
    @Sql(scripts = ["/db/events.sql"], config = SqlConfig(encoding = "UTF-8"))
    fun shouldCreateByGuest() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 22, 11, 12, 0, 0)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        val eventDto =
            getEventDto(name = "Event 1", slug = "event-1", guestName = "John Doe", guestEmail = "john.doe@example.com")
        // when
        val event = restApiTemplate.postForEntity(EVENTS_URI, eventDto, EventDto::class.java)
        // then
        assertNotNull(event)
        assertEquals(HttpStatus.CREATED, event?.statusCode)
        Assertions.assertThat(event.body)
            .hasFieldOrPropertyWithValue("name", eventDto.name)
            .hasFieldOrPropertyWithValue("slug", eventDto.slug)
            .hasFieldOrPropertyWithValue("sender.name", eventDto.guestName)
            .hasFieldOrPropertyWithValue("sender.email", eventDto.guestEmail)
            .hasFieldOrPropertyWithValue("sender.userId", null)
            .hasFieldOrPropertyWithValue("createdAt", localDateTime)
    }

    @Test
    @Sql(scripts = ["/db/events.sql"], config = SqlConfig(encoding = "UTF-8"))
    fun shouldReturnEventDetailsById() {
        // given
        // when
        val result = restApiTemplate.getForEntity(
            String.format(EVENTS_DETAILS_URI, "kopernik-i-jego-swiat"),
            EventDto::class.java,
        )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.OK, result?.statusCode)
        Assertions.assertThat(result.body!!)
            .hasFieldOrPropertyWithValue("name", "Kopernik i jego świat")
            .hasFieldOrPropertyWithValue("slug", "kopernik-i-jego-swiat")
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 27, 7, 55, 20, 0))
            .hasFieldOrPropertyWithValue("updatedAt", LocalDateTime.of(2023, 5, 27, 8, 2, 35, 0))
    }

    @Test
    fun shouldReturnNotFoundWhenTryGetEventDetailsById() {
        // given
        // when
        val result =
            Assertions.catchThrowableOfType(
                { restApiTemplate.getForEntity(String.format(EVENTS_DETAILS_URI, "100"), Object::class.java) },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.NOT_FOUND, result?.statusCode)
    }

    @Test
    @Sql(scripts = ["/db/users.sql", "/db/roles.sql", "/db/users_roles.sql"], config = SqlConfig(encoding = "UTF-8"))
    fun shouldReturnInternalServerErrorWhenTryUpdate() {
        // given
        // when
        val result =
            Assertions.catchThrowableOfType(
                {
                    authorizedRestTemplate.put(
                        String.format(EVENTS_DETAILS_URI, "event-1"),
                        HashMap<String, Any>(),
                        Any::class.java
                    )
                },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.BAD_REQUEST, result?.statusCode)
    }

    @Test
    @Sql(scripts = ["/db/users.sql", "/db/roles.sql", "/db/users_roles.sql"], config = SqlConfig(encoding = "UTF-8"))
    fun shouldReturnNotFoundWhenTryUpdateByEventNotExist() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 22, 11, 12, 0, 0)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        val eventDto = getEventDto("Event 1", "event-1")
        // when
        val requestEntity = HttpEntity(eventDto)
        val result =
            Assertions.catchThrowableOfType(
                {
                    authorizedRestTemplate.exchange(
                        String.format(EVENTS_DETAILS_URI, "event-not-exist"),
                        HttpMethod.PUT,
                        requestEntity,
                        NewEventDto::class.java,
                    )
                },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.NOT_FOUND, result?.statusCode)
    }

    @Test
    @Sql(
        scripts = ["/db/events.sql", "/db/users.sql", "/db/roles.sql", "/db/users_roles.sql"],
        config = SqlConfig(encoding = "UTF-8")
    )
    fun shouldReturnBadRequestWhenTryUpdateButEventSlugExist() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 22, 11, 12, 0, 0)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        val eventDto = NewEventDto("Event 1", "dzien-dziecka-w-parku-szczesliwickim")
        // when
        val requestEntity = HttpEntity(eventDto)
        val result =
            Assertions.catchThrowableOfType(
                {
                    authorizedRestTemplate.exchange(
                        String.format(EVENTS_DETAILS_URI, "pajaki-i-skorpiony"),
                        HttpMethod.PUT,
                        requestEntity,
                        NewEventDto::class.java
                    )
                },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.BAD_REQUEST, result?.statusCode)
    }

    @ParameterizedTest
    @MethodSource("provideEventDtoList")
    @Sql(scripts = ["/db/users.sql", "/db/roles.sql", "/db/users_roles.sql"], config = SqlConfig(encoding = "UTF-8"))
    fun shouldReturnBadRequestWhenTryUpdate(eventName: String, eventSlug: String?, message: String) {
        // given
        val eventDto = NewEventDto(eventName, eventSlug)
        // when
        val result =
            Assertions.catchThrowableOfType(
                {
                    authorizedRestTemplate.put(
                        String.format(EVENTS_DETAILS_URI, "pajaki-i-skorpiony"),
                        eventDto,
                        Any::class.java
                    )
                },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.BAD_REQUEST, result!!.statusCode)
        assertTrue(result.message!!.contains(message))
    }

    @ParameterizedTest
    @MethodSource("provideEventsDtoValidList")
    @Sql(
        scripts = ["/db/events.sql", "/db/users.sql", "/db/roles.sql", "/db/users_roles.sql"],
        config = SqlConfig(encoding = "UTF-8")
    )
    fun shouldUpdate(eventName: String, eventSlug: String?, expectedEventSlug: String) {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 27, 8, 55, 0, 0)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        val eventDto = NewEventDto(eventName, eventSlug)
        // when
        val requestEntity = HttpEntity(eventDto)
        val event: ResponseEntity<EventDto> =
            authorizedRestTemplate.exchange(
                String.format(EVENTS_DETAILS_URI, "pajaki-i-skorpiony"),
                HttpMethod.PUT,
                requestEntity,
                EventDto::class.java
            )
        // then
        assertNotNull(event)
        assertEquals(HttpStatus.OK, event.statusCode)
        Assertions.assertThat(event.body)
            .hasFieldOrPropertyWithValue("name", eventName)
            .hasFieldOrPropertyWithValue("slug", expectedEventSlug)
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 27, 7, 55, 0, 0))
            .hasFieldOrPropertyWithValue("updatedAt", localDateTime)
    }

    @Test
    @Sql(
        scripts = ["/db/events.sql", "/db/users.sql", "/db/roles.sql", "/db/users_roles.sql"],
        config = SqlConfig(encoding = "UTF-8")
    )
    fun shouldDelete() {
        // given
        // when
        val result = authorizedRestTemplate.exchange(
            String.format(EVENTS_DETAILS_URI, "pajaki-i-skorpiony"),
            HttpMethod.DELETE,
            null,
            Any::class.java
        )
        // then
        assertEquals(HttpStatus.OK, result.statusCode)
        val entity = dbUtils.em().find(EventEntity::class.java, "pajaki-i-skorpiony")
        assertNull(entity)
    }

    @Test
    @Sql(scripts = ["/db/users.sql", "/db/roles.sql", "/db/users_roles.sql"], config = SqlConfig(encoding = "UTF-8"))
    fun shouldReturnNotFoundWhenTryDeleteByEventNotExist() {
        // given
        // when
        val result =
            Assertions.catchThrowableOfType(
                { restApiTemplate.getForEntity(String.format(EVENTS_DETAILS_URI, "100"), Object::class.java) },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.NOT_FOUND, result?.statusCode)
    }

    companion object {
        @JvmStatic
        private fun provideEventDtoList(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("Na", null, "newEventDto.name"),
                Arguments.of("Event", "ro", "newEventDto.slug"),
                Arguments.of(
                    "VeryLongEventNameVeryLongEventNameVeryLongEventNameVeryLongEventNameVeryLongEventNameVeryLongEventNameVeryLongEventName",
                    null,
                    "newEventDto.name"
                ),
                Arguments.of(
                    "Event Ok",
                    "VeryLongEventNameVeryLongEventNameVeryLongEventNameVeryLongEventNameVeryLongEventNameVeryLongEventNameVeryLongEventName",
                    "newEventDto.slug"
                )
            )
        }

        @JvmStatic
        private fun provideEventsDtoValidList(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("NewEventName", null, "neweventname"),
                Arguments.of("NewEventName", "new-event-name", "new-event-name"),
                Arguments.of("Kopernik i jego świat", null, "kopernik-i-jego-swiat1"),
            )
        }
    }

    private fun getEventDto(
        name: String,
        slug: String,
        description: String? = null,
        content: String? = null,
        online: Boolean = false,
        website: String? = null,
        ticketWebsite: String? = null,
        promo: Boolean = false,
        active: Boolean = false,
        address: AddressDto? = null,
        location: GeoLocationDto? = null,
        guestName: String? = null,
        guestEmail: String? = null,
    ) =
        NewEventDto(
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
            guestName = guestName,
            guestEmail = guestEmail
        )
}