package pl.wydarzeniawokolicy.domain.shared


open class ModelException(message: String) : RuntimeException(message)
open class NotFoundException(message: String) : ModelException(message)
