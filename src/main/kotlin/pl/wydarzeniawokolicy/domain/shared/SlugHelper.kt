package pl.wydarzeniawokolicy.domain.shared

class SlugHelper {
    companion object {
        fun generateSlug(repository: SlugRepository, slug: String): String {
            var i = 0
            var newSlug: String?
            do {
                newSlug = getSlug(slug, i)
                val exists = repository.existsBySlug(newSlug)
                i++
            } while (exists)
            return newSlug!!
        }

        private fun getSlug(slug: String, index: Int): String {
            return when (index) {
                0 -> slug
                else -> slug.plus(index)
            }
        }
    }
}