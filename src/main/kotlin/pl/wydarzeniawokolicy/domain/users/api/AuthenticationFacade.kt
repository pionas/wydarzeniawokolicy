package pl.wydarzeniawokolicy.domain.users.api

import pl.wydarzeniawokolicy.infrastructure.security.CustomUserDetails

interface AuthenticationFacade {

    fun isAuthentication(): Boolean
    fun getUserDetails(): CustomUserDetails
}