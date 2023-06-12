package pl.wydarzeniawokolicy.domain.events.api

import pl.wydarzeniawokolicy.domain.shared.BasicModel
import pl.wydarzeniawokolicy.infrastructure.database.events.AddressEmbeddable
import pl.wydarzeniawokolicy.infrastructure.database.events.EventEntity
import pl.wydarzeniawokolicy.infrastructure.database.events.GeoLocationEmbeddable
import pl.wydarzeniawokolicy.infrastructure.database.events.SenderEmbeddable
import java.time.LocalDateTime

class NewEvent(
    val name: String,
    var slug: String? = null,
    var description: String? = null,
    var content: String? = null,
    var online: Boolean = false,
    var website: String? = null,
    var ticketWebsite: String? = null,
    var promo: Boolean = false,
    var active: Boolean = false,
    var address: Address? = null,
    var location: GeoLocation? = null,
    var sender: Sender
)

class Address(
    var country: String? = null,
    var voivodeship: String? = null,
    var city: String? = null,
    var street: String? = null,
    var apartment: String? = null
) {
    constructor(addressEmbeddable: AddressEmbeddable) : this(
        country = addressEmbeddable.country,
        voivodeship = addressEmbeddable.voivodeship,
        city = addressEmbeddable.city,
        street = addressEmbeddable.street,
        apartment = addressEmbeddable.apartment,
    )
}

class GeoLocation(var coordinatesLatitude: Float? = null, var coordinatesLongitude: Float? = null) {
    constructor(geoLocationEmbeddable: GeoLocationEmbeddable) : this(
        coordinatesLatitude = geoLocationEmbeddable.coordinatesLatitude,
        coordinatesLongitude = geoLocationEmbeddable.coordinatesLongitude
    )
}

class Sender(var name: String? = null, var email: String? = null, var userId: Long? = null) {
    constructor(senderEmbeddable: SenderEmbeddable) : this(
        name = senderEmbeddable.name,
        email = senderEmbeddable.email,
        userId = senderEmbeddable.userId
    )
}

class Event(
    var name: String,
    var slug: String,
    var description: String?,
    var content: String?,
    var online: Boolean,
    var website: String?,
    var ticketWebsite: String?,
    var promo: Boolean,
    var active: Boolean,
    var address: Address?,
    var location: GeoLocation?,
    var sender: Sender,
    createdAt: LocalDateTime,
    updatedAt: LocalDateTime? = null,
    deletedAt: LocalDateTime? = null
) : BasicModel(createdAt, updatedAt, deletedAt) {
    constructor(entity: EventEntity) : this(
        name = entity.name,
        slug = entity.slug,
        description = entity.description,
        content = entity.content,
        online = entity.online,
        website = entity.website,
        ticketWebsite = entity.ticketWebsite,
        promo = entity.promo,
        active = entity.active,
        address = entity.address?.let { Address(it) },
        location = entity.location?.let { GeoLocation(it) },
        sender = Sender(entity.sender),
        createdAt = entity.createdAt,
        updatedAt = entity.updatedAt,
        deletedAt = entity.deletedAt,
    )
}