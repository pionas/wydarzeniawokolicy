package pl.wydarzeniawokolicy.infrastructure.shared

import org.apache.commons.io.FileUtils.copyInputStreamToFile
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import pl.wydarzeniawokolicy.domain.shared.FileUtils
import pl.wydarzeniawokolicy.domain.shared.StringUtils
import java.math.BigInteger
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.security.MessageDigest

@Component
class FileUtilsImpl(val stringUtils: StringUtils) : FileUtils {

    override fun upload(uploadDirectory: String, file: MultipartFile): String {
        val uploadsFolderPath: Path = Paths.get(uploadDirectory)
        val fileName = stringUtils.randomAlphanumeric(10).plus("_").plus(file.originalFilename)
        val targetFile = uploadsFolderPath.resolve(fileName).toFile()
        copyInputStreamToFile(file.inputStream, targetFile)
        return uploadDirectory.plus("/").plus(fileName)
    }

    override fun getHash(filePath: String): String {
        val data: ByteArray = Files.readAllBytes(Paths.get(filePath))
        val hash: ByteArray = MessageDigest.getInstance("MD5").digest(data)
        return BigInteger(1, hash).toString(16)
    }
}