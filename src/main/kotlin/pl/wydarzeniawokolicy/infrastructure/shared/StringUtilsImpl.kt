package pl.wydarzeniawokolicy.infrastructure.shared

import org.apache.commons.lang3.RandomStringUtils
import org.springframework.stereotype.Component
import pl.wydarzeniawokolicy.domain.shared.StringUtils

@Component
class StringUtilsImpl : StringUtils {

    override fun randomAlphanumeric(count: Int): String = RandomStringUtils.randomAlphanumeric(count)
}