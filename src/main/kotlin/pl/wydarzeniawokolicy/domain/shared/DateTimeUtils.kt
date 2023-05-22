package pl.wydarzeniawokolicy.domain.shared

import java.time.LocalDate
import java.time.LocalDateTime

interface DateTimeUtils {

    fun getLocalDateTimeNow(): LocalDateTime
    fun getLocalDateNow(): LocalDate
}