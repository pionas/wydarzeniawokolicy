package pl.wydarzeniawokolicy.infrastructure.shared

import org.springframework.stereotype.Component
import pl.wydarzeniawokolicy.domain.shared.DateTimeUtils
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class DateTimeUtilsImpl : DateTimeUtils {
    override fun getLocalDateTimeNow(): LocalDateTime = LocalDateTime.now()

    override fun getLocalDateNow(): LocalDate = LocalDate.now()
}