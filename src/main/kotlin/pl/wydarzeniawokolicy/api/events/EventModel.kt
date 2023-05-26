package pl.wydarzeniawokolicy.api.events

import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class EventDto(
    val name: String,
    val slug: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
    val deletedAt: LocalDateTime?
)

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class NewEventDto(
    @field:NotEmpty @field:Size(min = 3, max = 103) var name: String,
    @field:Size(min = 3, max = 103) var slug: String?,
)