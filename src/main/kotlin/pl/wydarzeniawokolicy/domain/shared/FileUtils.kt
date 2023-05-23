package pl.wydarzeniawokolicy.domain.shared

import org.springframework.web.multipart.MultipartFile

interface FileUtils {

    fun saveFile(uploadDirectory: String, file: MultipartFile): String
    fun getHash(filePath: String): String
}