package pl.wydarzeniawokolicy.web

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.RememberMeServices
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices.RememberMeTokenAlgorithm
import org.springframework.security.web.savedrequest.NullRequestCache
import org.springframework.security.web.savedrequest.RequestCache

@Configuration
class WebSecurityConfig {
    @Bean
    fun webFilterChain(http: HttpSecurity, rememberMeServices: RememberMeServices): SecurityFilterChain {
        val nullRequestCache: RequestCache = NullRequestCache()
        http.authorizeHttpRequests { it.anyRequest().permitAll() }
        http.requestCache { it.requestCache(nullRequestCache) }
        http.rememberMe { it.rememberMeServices(rememberMeServices) }
        return http.build()
    }

    @Bean
    fun rememberMeServices(userDetailsService: UserDetailsService): RememberMeServices {
        val encodingAlgorithm = RememberMeTokenAlgorithm.SHA256
        val rememberMe = TokenBasedRememberMeServices("ASASAS", userDetailsService, encodingAlgorithm)
        rememberMe.setMatchingAlgorithm(RememberMeTokenAlgorithm.MD5)
        return rememberMe
    }
}