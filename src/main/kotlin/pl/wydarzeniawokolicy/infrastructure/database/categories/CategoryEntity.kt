package pl.wydarzeniawokolicy.infrastructure.database.categories

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.DynamicUpdate
import pl.wydarzeniawokolicy.domain.categories.api.Category
import java.time.LocalDateTime

@Entity
@Table(name = "categories")
@DynamicUpdate
data class CategoryEntity(

    @Id
    var slug: String,
    var name: String,
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime?,
    var deletedAt: LocalDateTime?
) {
    fun update(category: Category) {
        this.slug = category.slug
        this.name = category.name
        this.createdAt = category.createdAt
        this.updatedAt = category.updatedAt
        this.deletedAt = category.deletedAt
    }
}