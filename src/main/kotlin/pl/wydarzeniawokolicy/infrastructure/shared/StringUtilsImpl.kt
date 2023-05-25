package pl.wydarzeniawokolicy.infrastructure.shared

import org.apache.commons.lang3.RandomStringUtils
import org.springframework.stereotype.Component
import pl.wydarzeniawokolicy.domain.shared.StringUtils
import java.text.Normalizer
import java.util.regex.Pattern


@Component
class StringUtilsImpl : StringUtils {

    private val NONLATIN: Pattern = Pattern.compile("[^\\w-]")
    private val WHITESPACE: Pattern = Pattern.compile("[\\s]")
    private val EDGESDHASHES = Pattern.compile("(^-|-$)")

    override fun randomAlphanumeric(count: Int): String = RandomStringUtils.randomAlphanumeric(count)
    override fun slug(slug: String): String {
        val nowhitespace = WHITESPACE.matcher(slug).replaceAll("-")
        val normalized: String = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD)
        var newSlug = NONLATIN.matcher(normalized).replaceAll("")
        newSlug = EDGESDHASHES.matcher(newSlug).replaceAll("")
        return newSlug.lowercase()
    }
}