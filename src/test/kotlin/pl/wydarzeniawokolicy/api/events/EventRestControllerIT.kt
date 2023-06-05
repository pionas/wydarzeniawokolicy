package pl.wydarzeniawokolicy.api.events

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
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
import pl.wydarzeniawokolicy.api.BasicIT
import pl.wydarzeniawokolicy.infrastructure.database.events.EventEntity
import java.time.LocalDateTime
import java.util.stream.Stream


internal class EventRestControllerIT : BasicIT() {

    @Test
    @Disabled
    fun shouldReturnUnauthorized() {
        // given

        // when
        val result = restApiTemplate.getForEntity("/events", List::class.java)

        // then
        assertNotNull(result)
        assertEquals(HttpStatus.UNAUTHORIZED, result?.statusCode)
    }

    @Test
    @Sql(scripts = ["/db/events.sql"], config = SqlConfig(encoding = "UTF-8"))
    fun shouldReturnEventList() {
        // given
        // when
        val result: ResponseEntity<List<EventDto>> = restApiTemplate.exchange(
            "/events",
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
    fun shouldReturnInternalServerErrorWhenTryCreate() {
        // given
        // when
        val result =
            Assertions.catchThrowableOfType(
                { restApiTemplate.postForEntity("/events", HashMap<String, Any>(), Any::class.java) },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.BAD_REQUEST, result?.statusCode)
    }

    @Test
    fun shouldReturnBadRequestWhenTryCreate() {
        // given
        val eventDto = NewEventDto("", null)
        // when
        val result =
            Assertions.catchThrowableOfType(
                { restApiTemplate.postForEntity("/events", eventDto, Any::class.java) },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.BAD_REQUEST, result?.statusCode)
    }

    @Test
    @Sql(scripts = ["/db/events.sql"], config = SqlConfig(encoding = "UTF-8"))
    fun shouldCreate() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 22, 11, 12, 0, 0)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        val eventDto = NewEventDto("Event 1", "event-1")
        // when
        val event = restApiTemplate.postForEntity("/events", eventDto, EventDto::class.java)
        // then
        assertNotNull(event)
        assertEquals(HttpStatus.CREATED, event?.statusCode)
        Assertions.assertThat(event.body)
            .hasFieldOrPropertyWithValue("name", eventDto.name)
            .hasFieldOrPropertyWithValue("slug", eventDto.slug)
            .hasFieldOrPropertyWithValue("createdAt", localDateTime)
    }

    @Test
    @Sql(scripts = ["/db/events.sql"], config = SqlConfig(encoding = "UTF-8"))
    fun shouldReturnEventDetailsById() {
        // given
        // when
        val result = restApiTemplate.getForEntity("/events/kopernik-i-jego-swiat", EventDto::class.java)
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
                { restApiTemplate.getForEntity("/events/100", Object::class.java) },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.NOT_FOUND, result?.statusCode)
    }

    @Test
    fun shouldReturnInternalServerErrorWhenTryUpdate() {
        // given
        // when
        val result =
            Assertions.catchThrowableOfType(
                { restApiTemplate.put("/events/event-1", HashMap<String, Any>(), Any::class.java) },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.BAD_REQUEST, result?.statusCode)
    }

    @Test
    fun shouldReturnNotFoundWhenTryUpdateByEventNotExist() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 22, 11, 12, 0, 0)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        val eventDto = NewEventDto("Event 1", "event-1")
        // when
        val requestEntity = HttpEntity(eventDto)
        val result =
            Assertions.catchThrowableOfType(
                {
                    restApiTemplate.exchange(
                        "/events/event-not-exist",
                        HttpMethod.PUT,
                        requestEntity,
                        NewEventDto::class.java
                    )
                },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.NOT_FOUND, result?.statusCode)
    }

    @Test
    @Sql("/db/events.sql")
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
                    restApiTemplate.exchange(
                        "/events/pajaki-i-skorpiony",
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
    fun shouldReturnBadRequestWhenTryUpdate(eventName: String, eventSlug: String?, message: String) {
        // given
        val eventDto = NewEventDto(eventName, eventSlug)
        // when
        val result =
            Assertions.catchThrowableOfType(
                { restApiTemplate.put("/events/pajaki-i-skorpiony", eventDto, Any::class.java) },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.BAD_REQUEST, result!!.statusCode)
        assertTrue(result.message!!.contains(message))
    }

    @ParameterizedTest
    @MethodSource("provideEventsDtoValidList")
    @Sql("/db/events.sql")
    fun shouldUpdate(eventName: String, eventSlug: String?, expectedEventSlug: String) {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 27, 8, 55, 0, 0)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        val eventDto = NewEventDto(eventName, eventSlug)
        // when
        val requestEntity = HttpEntity(eventDto)
        val event: ResponseEntity<EventDto> =
            restApiTemplate.exchange("/events/pajaki-i-skorpiony", HttpMethod.PUT, requestEntity, EventDto::class.java)
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
    @Sql("/db/events.sql")
    fun shouldDelete() {
        // given
        // when
        val result = restApiTemplate.exchange("/events/pajaki-i-skorpiony", HttpMethod.DELETE, null, Any::class.java)
        // then
        assertEquals(HttpStatus.OK, result.statusCode)
        val entity = dbUtils.em().find(EventEntity::class.java, "pajaki-i-skorpiony")
        assertNull(entity)
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
}