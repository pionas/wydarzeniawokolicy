package pl.wydarzeniawokolicy.domain.shared

import org.springframework.web.multipart.MultipartFile

interface FileUtils {

    fun upload(uploadDirectory: String, file: MultipartFile): String
    fun getHash(filePath: String): String
}