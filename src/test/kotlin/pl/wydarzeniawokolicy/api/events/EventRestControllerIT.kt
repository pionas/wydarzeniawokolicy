package pl.wydarzeniawokolicy.api.events

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.web.client.HttpClientErrorException
import pl.wydarzeniawokolicy.api.BasicIT
import java.time.LocalDateTime


internal class EventRestControllerIT : BasicIT() {

    @Test
    @Disabled
    fun shouldReturnUnauthorized() {
        // given

        // when
        val result = restTemplate.getForEntity("/events", List::class.java)

        // then
        assertNotNull(result)
        assertEquals(HttpStatus.UNAUTHORIZED, result?.statusCode)
    }

    @Test
    @Sql(scripts = ["/db/events.sql"], config = SqlConfig(encoding = "UTF-8"))
    fun shouldReturnEventList() {
        // given
        // when
        val result: ResponseEntity<List<EventDto>> = restTemplate.exchange(
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
                { restTemplate.postForEntity("/events", HashMap<String, Any>(), Any::class.java) },
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
                { restTemplate.postForEntity("/events", eventDto, Any::class.java) },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.BAD_REQUEST, result?.statusCode)
    }

    @Test
    fun shouldCreate() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 22, 11, 12, 0, 0)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        val eventDto = NewEventDto("Event 1", "event-1")
        // when
        val event = restTemplate.postForEntity("/events", eventDto, EventDto::class.java)
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
        val result = restTemplate.getForEntity("/events/kopernik-i-jego-swiat", EventDto::class.java)
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
                { restTemplate.getForEntity("/events/100", Object::class.java) },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.NOT_FOUND, result?.statusCode)
    }

    @Test
    fun shouldUpdateEventDetails() {
    }

    @Test
    fun shouldReturnNotFoundWhenTryUpdateEventDetailsButEventNotExist() {
    }

    @Test
    fun shouldReturnNotFoundWhenTryUpdateEventDetails() {
        // null, empty invalid name, email, password, passwordConfirm
    }

    @Test
    fun shouldDelete() {
        // given
        // when
        val result = restTemplate.exchange("/events/100", HttpMethod.DELETE, null, Any::class.java)
        // then
        assertEquals(HttpStatus.OK, result.statusCode)
    }
}