package pl.wydarzeniawokolicy.infrastructure.database.roles

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import pl.wydarzeniawokolicy.infrastructure.database.users.UserEntity
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
) {
    @ManyToMany(mappedBy = "roles")
    val users: Set<UserEntity> = emptySet()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RoleEntity

        return slug == other.slug
    }

    override fun hashCode(): Int {
        return slug.hashCode()
    }

}