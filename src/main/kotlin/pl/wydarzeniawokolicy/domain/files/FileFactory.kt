package pl.wydarzeniawokolicy.domain.files

import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import pl.wydarzeniawokolicy.domain.files.api.File
import pl.wydarzeniawokolicy.domain.shared.DateTimeUtils
import pl.wydarzeniawokolicy.domain.shared.FileUtils
import java.time.format.DateTimeFormatter

@Component
class FileFactory(
    val dateTimeUtils: DateTimeUtils,
    val fileUtils: FileUtils
) {

    val fileUploadFolderPath = "uploads/files/unsecure/"

    fun create(file: MultipartFile): File {
        val filePath = fileUtils.upload(getFolderPath(), file)

        return File(
            fileUtils.getHash(filePath),
            file.originalFilename!!,
            filePath,
            dateTimeUtils.getLocalDateTimeNow(),
            null,
            null
        )
    }

    private fun getFolderPath(): String {
        return fileUploadFolderPath.plus(
            dateTimeUtils.getLocalDateNow().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
        )
    }

}
