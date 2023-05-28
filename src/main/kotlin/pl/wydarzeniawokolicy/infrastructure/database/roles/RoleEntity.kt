package pl.wydarzeniawokolicy.infrastructure.database.roles

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "roles")
data class RoleEntity(

    @Id
    var slug: String,
    var name: String,
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime?,
    var deletedAt: LocalDateTime?
)