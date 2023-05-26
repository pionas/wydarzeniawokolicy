package pl.wydarzeniawokolicy.api.events

import org.mapstruct.Mapper
import pl.wydarzeniawokolicy.domain.events.api.Event
import pl.wydarzeniawokolicy.domain.events.api.NewEvent
import pl.wydarzeniawokolicy.domain.files.api.File

@Mapper(componentModel = "spring")
interface EventMapper {

    fun mapToDto(event: Event): EventDto
    fun mapToDto(events: List<Event>): List<EventDto>
    fun mapToDomain(event: NewEventDto): NewEvent

}
