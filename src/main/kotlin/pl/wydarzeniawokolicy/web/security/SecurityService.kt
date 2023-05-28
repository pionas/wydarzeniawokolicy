package pl.wydarzeniawokolicy.web.security

import com.vaadin.flow.spring.security.AuthenticationContext
import org.springframework.stereotype.Component
import pl.wydarzeniawokolicy.infrastructure.security.CustomUserDetails
import kotlin.jvm.optionals.getOrNull

@Component
class SecurityService(val authenticationContext: AuthenticationContext) {

    fun getAuthenticatedUser(): CustomUserDetails? {
        return authenticationContext.getAuthenticatedUser(CustomUserDetails::class.java).getOrNull()
    }

    fun logout() {
        authenticationContext.logout()
    }
}