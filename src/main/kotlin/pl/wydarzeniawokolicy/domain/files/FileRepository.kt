package pl.wydarzeniawokolicy.domain.files

import pl.wydarzeniawokolicy.domain.files.api.File

interface FileRepository {

    fun findAll(): List<File>
    fun save(file: File): File
    fun findByHash(hash: String): File?
    fun deleteByHash(hash: String)
}