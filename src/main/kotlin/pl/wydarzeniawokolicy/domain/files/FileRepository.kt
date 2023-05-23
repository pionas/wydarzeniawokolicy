package pl.wydarzeniawokolicy.domain.files

import pl.wydarzeniawokolicy.domain.files.api.File
import java.util.*

interface FileRepository {

    fun findAll(): List<File>
    fun save(file: File): File
    fun findByHash(hash: String): Optional<File>
    fun deleteByHash(hash: String)
}