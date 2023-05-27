package pl.wydarzeniawokolicy.infrastructure.security

import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.InternalAuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import pl.wydarzeniawokolicy.domain.users.UserRepository
import pl.wydarzeniawokolicy.domain.users.api.User

class CustomAuthenticationProvider(
    private val repository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : AbstractUserDetailsAuthenticationProvider() {
    private var userNotFoundEncodedPassword = "userNotFoundPassword"

    override fun additionalAuthenticationChecks(
        userDetails: UserDetails,
        authentication: UsernamePasswordAuthenticationToken
    ) {
        if (authentication.credentials == null) {
            logger.debug("Failed to authenticate since no credentials provided")
            throw BadCredentialsException(
                messages
                    .getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials")
            )
        }
        verifyPassword(authentication, userDetails)
    }

    override fun retrieveUser(username: String, authentication: UsernamePasswordAuthenticationToken): UserDetails {
        try {
            val user: User = username.let { repository.findByName(it) } ?: throw UsernameNotFoundException(username)
            return CustomUserDetails(user)
        } catch (ex: UsernameNotFoundException) {
            mitigateAgainstTimingAttack(authentication)
            throw ex
        } catch (ex: InternalAuthenticationServiceException) {
            throw ex
        } catch (ex: Exception) {
            throw InternalAuthenticationServiceException(ex.message, ex)
        }
    }

    private fun mitigateAgainstTimingAttack(authentication: UsernamePasswordAuthenticationToken) {
        if (authentication.credentials != null) {
            val presentedPassword = authentication.credentials.toString()
            passwordEncoder.matches(presentedPassword, userNotFoundEncodedPassword)
        }
    }

    private fun verifyPassword(
        authentication: UsernamePasswordAuthenticationToken,
        userDetails: UserDetails
    ) {
        val presentedPassword = getPresentedPassword(authentication, userDetails)
        if (!passwordEncoder.matches(presentedPassword, userDetails.password)) {
            logger.debug("Failed to authenticate since password does not match stored value")
            throw BadCredentialsException(
                messages
                    .getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials")
            )
        }
    }

    private fun getPresentedPassword(
        authentication: UsernamePasswordAuthenticationToken,
        userDetails: UserDetails
    ): String {
        var presentedPassword = authentication.credentials.toString()
        if (userDetails is CustomUserDetails) {
            presentedPassword = presentedPassword.plus(userDetails.getSalt())
        }
        return presentedPassword
    }

}