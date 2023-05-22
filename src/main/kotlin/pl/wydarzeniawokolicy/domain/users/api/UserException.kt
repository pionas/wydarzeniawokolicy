package pl.wydarzeniawokolicy.domain.users.api


open class UserException(message: String) : RuntimeException(message)
class UserEmailExistException(email: String) : UserException("User by email $email is exist!")
class UserNameExistException(name: String) : UserException("User by name $name is exist!")
class UserPasswordCompareException : UserException("Passwords do not match")

class UserInvalidPasswordException : UserException("Invalid password")
class UserNotFoundException(id: Long) : UserException("User by ID $id not exists")
