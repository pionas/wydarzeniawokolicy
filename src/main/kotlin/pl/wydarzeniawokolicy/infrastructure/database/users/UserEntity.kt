package pl.wydarzeniawokolicy.infrastructure.database.users

import jakarta.persistence.*
import pl.wydarzeniawokolicy.infrastructure.database.roles.RoleEntity
import java.time.LocalDateTime


@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,
    var name: String,
    var email: String,
    var password: String,
    var salt: String,
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime?,
    var deletedAt: LocalDateTime?
) {
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "users_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    val roles: Set<RoleEntity> = emptySet()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserEntity

        return name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }


}