package pl.wydarzeniawokolicy.infrastructure

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.core.GrantedAuthorityDefaults
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import pl.wydarzeniawokolicy.domain.users.UserRepository
import pl.wydarzeniawokolicy.infrastructure.security.CustomAuthenticationProvider


@Configuration(proxyBeanMethods = false)
@EnableMethodSecurity(securedEnabled = true)
class SecurityConfig {

    @Value("\${spring.security.debug:false}")
    var securityDebug = false

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun grantedAuthorityDefaults(): GrantedAuthorityDefaults {
        return GrantedAuthorityDefaults("") // Remove the ROLE_ prefix
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.httpBasic().and()
            .anonymous()
            .and()
            .authorizeHttpRequests()
            .requestMatchers("/**").permitAll()
            .and()
            .formLogin().loginPage("/login").permitAll()
            .and()
            .logout().logoutSuccessUrl("/")
            .and()
            .csrf().disable()
            .cors().disable()
            .headers().disable()
        return http.build()
    }

    @Bean
    fun authenticationProvider(
        repository: UserRepository,
        passwordEncoder: PasswordEncoder
    ): AbstractUserDetailsAuthenticationProvider {
        return CustomAuthenticationProvider(repository, passwordEncoder)
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web: WebSecurity ->
            web.debug(securityDebug)
                .ignoring()
                .requestMatchers("/css/**", "/js/**", "/img/**", "/lib/**", "/favicon.ico")
        }
    }
}