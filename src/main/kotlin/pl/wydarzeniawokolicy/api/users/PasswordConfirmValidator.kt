package pl.wydarzeniawokolicy.api.users

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class PasswordConfirmValidator : ConstraintValidator<PasswordConfirmAnnotation, UserDetailsDto> {

    override fun isValid(userDetailsDto: UserDetailsDto?, context: ConstraintValidatorContext?): Boolean {
        return userDetailsDto?.password == userDetailsDto?.passwordConfirm
    }
}
