package pl.wydarzeniawokolicy.api.users

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class SignUpDto(
    @field:NotEmpty @field:Size(min = 3, max = 50) var name: String?,
    @field:NotEmpty @field:Email var email: String?,
    @field:NotEmpty @field:Size(min = 5) var password: String?,
    @field:NotEmpty @field:Size(min = 5) @JsonProperty(value = "passwordConfirm") var passwordConfirm: String?
)

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class UserDto(
    val id: Long,
    val name: String,
    val email: String?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
    val deletedAt: LocalDateTime?
)

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@PasswordConfirmAnnotation
data class UserDetailsDto(
    @field:NotEmpty @field:Size(min = 3, max = 50) val name: String,
    @field:NotEmpty @field:Email val email: String,
    @field:NotEmpty @field:Size(min = 5) val oldPassword: String,
    @field:Size(min = 5) val password: String?,
    @field:Size(min = 5) val passwordConfirm: String?
)