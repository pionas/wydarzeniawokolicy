package pl.wydarzeniawokolicy.domain.files.api

open class FileException(message: String) : RuntimeException(message)
class FileNotFoundException(hash: String) : FileException("File by hash $hash not exists")
