package pl.wydarzeniawokolicy.infrastructure.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import pl.wydarzeniawokolicy.domain.users.api.User

class CustomUserDetails(val user: User) : UserDetails {

    fun getId() = user.id
    fun getSalt() = user.salt

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        val list: MutableList<GrantedAuthority> = ArrayList()
        user.roles?.forEach {
            list.add(SimpleGrantedAuthority(it.name))
        }
        return list
    }

    override fun getPassword() = user.password

    override fun getUsername() = user.name

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = user.deletedAt == null

}