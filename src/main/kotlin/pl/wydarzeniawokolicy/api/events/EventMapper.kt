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
    @Mapping(target = "sender.name", source = "senderDto.name")
    @Mapping(target = "sender.email", source = "senderDto.email")
    @Mapping(target = "sender.userId", source = "senderDto.userId")
    fun mapToDomain(event: NewEventDto, senderDto: SenderDto): NewEvent

}
