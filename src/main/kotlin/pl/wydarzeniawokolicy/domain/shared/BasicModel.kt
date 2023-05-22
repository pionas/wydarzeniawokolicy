package pl.wydarzeniawokolicy.domain.shared

import java.time.LocalDateTime

open class BasicModel(
    val createdAt: LocalDateTime,
    var updatedAt: LocalDateTime?,
    var deletedAt: LocalDateTime?
)