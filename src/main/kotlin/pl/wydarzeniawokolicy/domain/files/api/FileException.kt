package pl.wydarzeniawokolicy.domain.files.api

import pl.wydarzeniawokolicy.domain.shared.NotFoundException

class FileNotFoundException(hash: String) : NotFoundException("File by hash $hash not exists")
