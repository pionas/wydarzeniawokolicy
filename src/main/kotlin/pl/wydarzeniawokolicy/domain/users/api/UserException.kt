package pl.wydarzeniawokolicy.domain.users.api

import pl.wydarzeniawokolicy.domain.shared.ModelException
import pl.wydarzeniawokolicy.domain.shared.NotFoundException

class UserEmailExistException(email: String) : ModelException("User by email $email is exist!")
class UserNameExistException(name: String) : ModelException("User by name $name is exist!")
class UserPasswordCompareException : ModelException("Passwords do not match")

class UserInvalidPasswordException : ModelException("Invalid password")
class UserNotFoundException(id: Long) : NotFoundException("User by ID $id not exists")
