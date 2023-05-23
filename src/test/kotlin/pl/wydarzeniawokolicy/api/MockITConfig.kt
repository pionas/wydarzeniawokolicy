package pl.wydarzeniawokolicy.api

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import pl.wydarzeniawokolicy.domain.shared.DateTimeUtils

@TestConfiguration
class MockITConfig {

    @MockBean
    private lateinit var dateTimeUtils: DateTimeUtils

}
