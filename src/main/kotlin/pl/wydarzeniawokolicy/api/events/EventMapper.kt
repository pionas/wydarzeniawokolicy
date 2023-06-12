package pl.wydarzeniawokolicy.api.events

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import pl.wydarzeniawokolicy.domain.events.api.Event
import pl.wydarzeniawokolicy.domain.events.api.NewEvent

@Mapper(componentModel = "spring")
interface EventMapper {

    fun mapToDto(event: Event): EventDto
    fun mapToDto(events: List<Event>): List<EventDto>

    @Mapping(target = "name", source = "event.name")
    @Mapping(target = "sender", source = "senderDto")
    @Mapping(target = "address", source = "event.address")
    @Mapping(target = "location", source = "event.location")
    fun mapToDomain(event: NewEventDto, senderDto: SenderDto): NewEvent

}
