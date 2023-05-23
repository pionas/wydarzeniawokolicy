package pl.wydarzeniawokolicy.api.users

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class SignUpDto(
    @field:NotEmpty @field:Size(min = 3, max = 50) var name: String?,
    @field:NotEmpty @field:Email var email: String?,
    @field:NotEmpty @field:Min(5) var password: String?,
    @field:NotEmpty @field:Min(5) @JsonProperty(value = "passwordConfirm") var passwordConfirm: String?
)

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class UserDto(
    val id: Long,
    val name: String,
    val email: String?,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class) @JsonSerialize(using = LocalDateTimeSerializer::class) val createdAt: LocalDateTime
)

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@PasswordConfirmAnnotation
data class UserDetailsDto(
    @field:NotEmpty @field:Size(min = 3, max = 50) val name: String,
    @field:NotEmpty @field:Email val email: String,
    @field:NotEmpty @field:Min(5) val oldPassword: String,
    @field:Min(5) val password: String?,
    @field:Min(5) val passwordConfirm: String?
)