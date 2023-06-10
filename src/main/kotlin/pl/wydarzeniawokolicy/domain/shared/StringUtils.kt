package pl.wydarzeniawokolicy.domain.shared

interface StringUtils {
    fun randomAlphanumeric(count: Int): String
    fun slug(slug: String, replacement: String? = "-"): String
}