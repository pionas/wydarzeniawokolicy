package pl.wydarzeniawokolicy.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import pl.wydarzeniawokolicy.WydarzeniaWOkolicyApplication


@SpringBootTest(
    classes = [WydarzeniaWOkolicyApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
class BasicIT {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

}
