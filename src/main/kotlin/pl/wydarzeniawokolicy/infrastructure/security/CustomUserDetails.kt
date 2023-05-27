package pl.wydarzeniawokolicy.infrastructure.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import pl.wydarzeniawokolicy.domain.users.api.User

class CustomUserDetails(val user: User) : UserDetails {

    fun getSalt() = user.salt

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        // TODO: after create role module should modified this list
        val list: MutableList<GrantedAuthority> = ArrayList()
        listOf("USER").forEach {
            list.add(SimpleGrantedAuthority(it))
        }
        if (user.name.lowercase().contains("admin")) {
            list.add(SimpleGrantedAuthority("VIEWER"))
        }
        return list
    }

    override fun getPassword() = user.password

    override fun getUsername() = user.name

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true

}