package pl.wydarzeniawokolicy.infrastructure.database.events

import jakarta.persistence.*
import pl.wydarzeniawokolicy.domain.events.api.Address
import pl.wydarzeniawokolicy.domain.events.api.GeoLocation
import pl.wydarzeniawokolicy.domain.events.api.Sender
import java.time.LocalDateTime

@Entity
@Table(name = "events")
data class EventEntity(

    @Id
    var slug: String,
    var name: String,
    var description: String?,
    var content: String?,
    var online: Boolean,
    var website: String?,
    var ticketWebsite: String?,
    var promo: Boolean,
    var active: Boolean,
    @Embedded
    var address: AddressEmbeddable? = null,
    @Embedded
    var location: GeoLocationEmbeddable?,

    @Embedded
    var sender: SenderEmbeddable,
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime?,
    var deletedAt: LocalDateTime?
)

@Embeddable
class AddressEmbeddable(
    var country: String?,
    var voivodeship: String?,
    var city: String?,
    var street: String?,
    var apartment: String?
) {
    constructor(address: Address) : this(
        country = address.country,
        voivodeship = address.voivodeship,
        city = address.city,
        street = address.street,
        apartment = address.apartment
    )
}

@Embeddable
class GeoLocationEmbeddable(geoLocation: GeoLocation) {

    var coordinatesLatitude: Float? = null
    var coordinatesLongitude: Float? = null

    init {
        coordinatesLatitude = geoLocation.coordinatesLatitude
        coordinatesLongitude = geoLocation.coordinatesLongitude
    }
}

@Embeddable
class SenderEmbeddable(sender: Sender) {

    @Column(name = "sender_name")
    var name: String? = null

    @Column(name = "sender_email")
    var email: String? = null

    @Column(name = "sender_user_id")
    var userId: Long? = null

    init {
        name = sender.name
        email = sender.email
        userId = sender.userId
    }
}