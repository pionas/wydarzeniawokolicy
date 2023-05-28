package pl.wydarzeniawokolicy.api.roles

import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class RoleDto(
    val name: String,
    val slug: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
    val deletedAt: LocalDateTime?
)

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class NewRoleDto(
    @field:NotEmpty @field:Size(min = 3, max = 50) var name: String?,
    @field:Size(min = 3, max = 53) var slug: String?,
)