package pl.wydarzeniawokolicy.api.events

import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import pl.wydarzeniawokolicy.domain.events.api.Address
import pl.wydarzeniawokolicy.domain.events.api.GeoLocation
import pl.wydarzeniawokolicy.domain.events.api.Sender
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class EventDto(
    var name: String,
    var slug: String,
    var description: String?,
    var content: String?,
    var online: Boolean,
    var website: String?,
    var ticketWebsite: String?,
    var promo: Boolean,
    var active: Boolean,
    var address: AddressDto?,
    var location: GeoLocationDto?,
    var sender: Sender,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
    val deletedAt: LocalDateTime?
)

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class NewEventDto(
    @field:NotEmpty @field:Size(min = 3, max = 103) var name: String,
    @field:Size(min = 3, max = 103) var slug: String?,
    var description: String? = null,
    var content: String? = null,
    var online: Boolean = false,
    var website: String? = null,
    var ticketWebsite: String? = null,
    var promo: Boolean = false,
    var active: Boolean = false,
    var address: Address? = null,
    var location: GeoLocation? = null
)

@JsonInclude(JsonInclude.Include.NON_EMPTY)
class AddressDto(
    var country: String?,
    var voivodeship: String?,
    var city: String?,
    var street: String?,
    var apartment: String?
)

@JsonInclude(JsonInclude.Include.NON_EMPTY)
class GeoLocationDto(var coordinatesLatitude: Float?, var coordinatesLongitude: Float?)

@JsonInclude(JsonInclude.Include.NON_EMPTY)
class SenderDto(var name: String? = null, var email: String? = null, var userId: Long? = null)