package pl.wydarzeniawokolicy.infrastructure.database.files

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "files")
data class FileEntity(

    @Id
    var hash: String,
    var name: String,
    var path: String,
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime?,
    var deletedAt: LocalDateTime?
)