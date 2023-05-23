package pl.wydarzeniawokolicy.api

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
class ApiSecurityConfig {

    @Bean
    fun apiFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests { it.anyRequest().permitAll() }
        return http.build()
    }
}