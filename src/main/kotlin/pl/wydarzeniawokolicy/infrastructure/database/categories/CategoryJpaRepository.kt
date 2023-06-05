package pl.wydarzeniawokolicy.infrastructure.database.categories

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface CategoryJpaRepository : CrudRepository<CategoryEntity, String> {

    fun existsBySlug(slug: String): Boolean

    @Modifying
    @Query("UPDATE CategoryEntity c set c.slug = :slug, c.name = :name, c.createdAt = :createdAt, c.updatedAt = :updatedAt, c.deletedAt = :deletedAt WHERE c.slug = :currentSlug")
    fun update(
        @Param("slug") slug: String,
        @Param("name") name: String,
        @Param("createdAt") createdAt: LocalDateTime,
        @Param("updatedAt") updatedAt: LocalDateTime?,
        @Param("deletedAt") deletedAt: LocalDateTime?,
        @Param("currentSlug") currentSlug: String
    )
}