package pl.wydarzeniawokolicy.domain.files.api

import org.springframework.web.multipart.MultipartFile

interface FileService {

    fun findAll(): List<File>
    fun create(file: MultipartFile): File
    fun findByHash(hash: String): File
    fun deleteByHash(hash: String)
}