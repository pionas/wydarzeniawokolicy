package pl.wydarzeniawokolicy.api.roles

import org.mapstruct.Mapper
import pl.wydarzeniawokolicy.domain.roles.api.NewRole
import pl.wydarzeniawokolicy.domain.roles.api.Role

@Mapper(componentModel = "spring")
interface RoleMapper {

    fun mapToDto(role: Role): RoleDto
    fun mapToDto(roles: List<Role>): List<RoleDto>
    fun mapToDomain(role: NewRoleDto): NewRole

}
