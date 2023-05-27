package pl.wydarzeniawokolicy.api

import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import pl.wydarzeniawokolicy.WydarzeniaWOkolicyApplication
import pl.wydarzeniawokolicy.domain.shared.DateTimeUtils


@SpringBootTest(
    classes = [WydarzeniaWOkolicyApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    properties = [
        "spring.profiles.active=it"
    ]
)
@Import(RestITConfig::class, MockITConfig::class, DbItConfig::class)
class BasicIT {

    @Qualifier("testRestTemplate")
    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Qualifier("authorizedRestTemplate")
    @Autowired
    lateinit var authorizedRestTemplate: TestRestTemplate

    @Qualifier("forbiddenRestTemplate")
    @Autowired
    lateinit var forbiddenRestTemplate: TestRestTemplate

    @Qualifier("wrongPasswordRestTemplate")
    @Autowired
    lateinit var wrongPasswordRestTemplate: TestRestTemplate

    @Autowired
    lateinit var dateTimeUtils: DateTimeUtils

    @Autowired
    lateinit var dbUtils: TestDbUtil

    @AfterEach
    fun tearDown() {
        dbUtils.clean()
    }
}
