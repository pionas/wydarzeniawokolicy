package pl.wydarzeniawokolicy.domain.shared

interface SlugRepository {

    fun existsBySlug(slug: String): Boolean
}