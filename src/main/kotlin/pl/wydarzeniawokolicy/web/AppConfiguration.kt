package pl.wydarzeniawokolicy.web

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class AppConfiguration {

    @Bean
    fun appProperties(@Value("\${spring.application.name}") appName: String, categoryProperties: CategoryProperties): AppProperties {
        return AppProperties(appName, categoryProperties)
    }
}
