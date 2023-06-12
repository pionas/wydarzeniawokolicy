package pl.wydarzeniawokolicy.api.events

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import pl.wydarzeniawokolicy.domain.events.api.EventService
import pl.wydarzeniawokolicy.domain.users.api.AuthenticationFacade

@RestController
@RequestMapping("/api/v1/events")
class EventRestController(
    private val service: EventService,
    private val mapper: EventMapper,
    private val authenticationFacade: AuthenticationFacade,
) {

    @GetMapping
    fun getAll(): List<EventDto> = mapper.mapToDto(service.findAll())

    @PostMapping
    fun create(@Valid @RequestBody event: NewEventDto): ResponseEntity<EventDto> {
        val createdEvent = service.create(mapper.mapToDomain(event, getSenderDetails(event)))
        return ResponseEntity(mapper.mapToDto(createdEvent), HttpStatus.CREATED)
    }

    @GetMapping("/{slug}")
    fun getById(@PathVariable("slug") slug: String): ResponseEntity<EventDto> {
        return ResponseEntity(mapper.mapToDto(service.findBySlug(slug)), HttpStatus.OK)
    }

    @PutMapping("/{slug}")
    @PreAuthorize("hasAnyRole('EVENT_MANAGEMENT','ADMIN','EVENT_UPDATE')")
    fun update(
        @PathVariable("slug") slug: String,
        @Valid @RequestBody event: NewEventDto,
        authentication: Authentication
    ): ResponseEntity<EventDto> {
        val createdRole = service.update(slug, mapper.mapToDomain(event, getSenderDetails(event)))
        return ResponseEntity(mapper.mapToDto(createdRole), HttpStatus.OK)
    }

    @DeleteMapping("/{slug}")
    @PreAuthorize("hasAnyRole('EVENT_MANAGEMENT','ADMIN','EVENT_DELETE')")
    fun delete(@PathVariable("slug") slug: String): ResponseEntity<Unit> {
        service.delete(slug)
        return ResponseEntity(HttpStatus.OK)
    }

    private fun getSenderDetails(event: NewEventDto): SenderDto {
        if (authenticationFacade.isAuthentication()) {
            val userDetails = authenticationFacade.getUserDetails()
            return SenderDto(userId = userDetails.getId())
        }
        return SenderDto(name = event.guestName, email = event.guestEmail)
    }

}