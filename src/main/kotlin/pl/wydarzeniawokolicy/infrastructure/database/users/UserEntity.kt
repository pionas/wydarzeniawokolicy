package pl.wydarzeniawokolicy.infrastructure.database.users

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,
    var name: String,
    var email: String,
    var password: String,
    var salt: String,
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime?,
    var deletedAt: LocalDateTime?
)