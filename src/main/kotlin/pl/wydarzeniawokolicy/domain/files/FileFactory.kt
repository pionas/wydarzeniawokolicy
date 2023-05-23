package pl.wydarzeniawokolicy.domain.files

import lombok.AllArgsConstructor
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import pl.wydarzeniawokolicy.domain.files.api.File
import pl.wydarzeniawokolicy.domain.shared.DateTimeUtils
import pl.wydarzeniawokolicy.domain.shared.FileUtils
import java.math.BigInteger
import java.nio.file.Files
import java.nio.file.Paths
import java.security.MessageDigest


@Component
@AllArgsConstructor
class FileFactory(
    val dateTimeUtils: DateTimeUtils,
    val fileUtils: FileUtils
) {
    val fileUploadFolderPath = "resources/uploads/unsecure"

    fun create(file: MultipartFile): File {
        val filePath = fileUtils.saveFile(fileUploadFolderPath, file)

        return File(
            fileUtils.getHash(filePath),
            file.originalFilename!!,
            filePath,
            dateTimeUtils.getLocalDateTimeNow(),
            null,
            null
        )
    }

}
