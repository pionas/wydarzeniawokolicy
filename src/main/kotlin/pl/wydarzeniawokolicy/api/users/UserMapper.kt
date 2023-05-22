package pl.wydarzeniawokolicy.api.users

import org.mapstruct.Mapper
import pl.wydarzeniawokolicy.domain.users.api.User
import pl.wydarzeniawokolicy.domain.users.api.UserDetails
import pl.wydarzeniawokolicy.domain.users.api.UserSignUp

@Mapper(componentModel = "spring")
interface UserMapper {

    fun mapToDto(user: User): UserDto
    fun mapToDto(users: List<User>): List<UserDto>
    fun mapToDomain(signUpDto: SignUpDto): UserSignUp
    fun mapToDomain(userDetailsDto: UserDetailsDto): UserDetails
}
