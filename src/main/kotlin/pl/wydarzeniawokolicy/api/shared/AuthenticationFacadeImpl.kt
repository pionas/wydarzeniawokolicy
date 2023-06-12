package pl.wydarzeniawokolicy.api.shared

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import pl.wydarzeniawokolicy.domain.users.api.AuthenticationFacade
import pl.wydarzeniawokolicy.infrastructure.security.CustomUserDetails
import java.util.*

@Component
class AuthenticationFacadeImpl : AuthenticationFacade {

    override fun isAuthentication(): Boolean {
        return Optional.of(SecurityContextHolder.getContext().authentication)
            .filter {
                return@filter it is UsernamePasswordAuthenticationToken
            }
            .map {
                return@map it.isAuthenticated
            }
            .orElse(false)
    }

    override fun getUserDetails(): CustomUserDetails {
        return SecurityContextHolder.getContext().authentication.principal as CustomUserDetails
    }
}