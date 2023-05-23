package pl.wydarzeniawokolicy.infrastructure.database.files

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "files")
data class FileEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var hash: String,
    var name: String,
    var path: String,
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime?,
    var deletedAt: LocalDateTime?
)