package pl.wydarzeniawokolicy.api.users

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class PasswordConfirmValidatorTest {

    private val passwordConfirmValidator = PasswordConfirmValidator()

    @Test
    fun shouldReturnValidIfPasswordAndPasswordConfirmIsNull() {
        //when
        val valid = passwordConfirmValidator.isValid(getUserDetailsDto(null, null), null)
        //then
        Assertions.assertTrue(valid)
    }

    @Test
    fun shouldReturnValidIfPasswordAndPasswordConfirmMatches() {
        //when
        val valid = passwordConfirmValidator.isValid(getUserDetailsDto("password123", "password123"), null)
        //then
        Assertions.assertTrue(valid)
    }

    @Test
    fun shouldReturnInvalidIfPasswordIsNotEmptyAndPasswordConfirmIsNull() {
        //when
        val valid = passwordConfirmValidator.isValid(getUserDetailsDto("password", null), null)
        //then
        Assertions.assertFalse(valid)
    }

    @Test
    fun shouldReturnInvalidIfPasswordIsNullAndPasswordConfirmIsNotEmpty() {
        //when
        val valid = passwordConfirmValidator.isValid(getUserDetailsDto(null, "passwordConfirm"), null)
        //then
        Assertions.assertFalse(valid)
    }

    @Test
    fun shouldReturnInvalidIfPasswordAndPasswordConfirmNotMatches() {
        //when
        val valid = passwordConfirmValidator.isValid(getUserDetailsDto("password", "passwordConfirm"), null)
        //then
        Assertions.assertFalse(valid)
    }

    private fun getUserDetailsDto(password: String?, passwordConfirm: String?): UserDetailsDto =
        UserDetailsDto("name", "email", "oldPassword", password, passwordConfirm)

}