package pl.wydarzeniawokolicy.api.users

import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class SignUpDto(
    @NotEmpty @Size(min = 3, max = 50) val name: String,
    @NotEmpty @Email val email: String,
    @NotEmpty @Min(5) val password: String,
    @NotEmpty val passwordConfirm: String
)

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class UserDto(val id: Long, val name: String, val email: String?, val createdAt: LocalDateTime?)

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@PasswordConfirmAnnotation
data class UserDetailsDto(
    @NotEmpty @Size(min = 3, max = 50) val name: String,
    @NotEmpty @Email val email: String,
    @NotEmpty @Min(5) val oldPassword: String,
    @Min(5) val password: String?,
    @Min(5) val passwordConfirm: String?
)