package pl.wydarzeniawokolicy.api.events

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.wydarzeniawokolicy.domain.events.api.EventService

@RestController
@RequestMapping("/api/v1/events")
class EventRestController(
    private val service: EventService,
    private val mapper: EventMapper,
) {

    @GetMapping("")
    fun getAll(): List<EventDto> = mapper.mapToDto(service.findAll())

    @PostMapping("")
    fun create(@Valid @RequestBody event: NewEventDto): ResponseEntity<EventDto> {
        val createdEvent = service.create(mapper.mapToDomain(event, getSenderDetails()))
        return ResponseEntity(mapper.mapToDto(createdEvent), HttpStatus.CREATED)
    }

    @GetMapping("/{slug}")
    fun getById(@PathVariable("slug") slug: String): ResponseEntity<EventDto> {
        return ResponseEntity(mapper.mapToDto(service.findBySlug(slug)), HttpStatus.OK)
    }

    @DeleteMapping("/{slug}")
    fun delete(@PathVariable("slug") slug: String): ResponseEntity<Void> {
        service.delete(slug)
        return ResponseEntity(HttpStatus.OK)
    }

    private fun getSenderDetails(): SenderDto = SenderDto(name = "John Doe", email = "john.doe@example.com")

}