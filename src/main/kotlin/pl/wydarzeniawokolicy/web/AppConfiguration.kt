package pl.wydarzeniawokolicy.web

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class AppConfiguration {

    @Value("\${spring.application.name}")
    var appName: String = ""
}
