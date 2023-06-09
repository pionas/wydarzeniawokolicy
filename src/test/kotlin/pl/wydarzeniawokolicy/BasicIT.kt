package pl.wydarzeniawokolicy

import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import pl.wydarzeniawokolicy.api.DbItConfig
import pl.wydarzeniawokolicy.api.MockITConfig
import pl.wydarzeniawokolicy.api.RestITConfig
import pl.wydarzeniawokolicy.api.TestDbUtil
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

    @Qualifier("restApiTemplate")
    @Autowired
    lateinit var restApiTemplate: TestRestTemplate

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
