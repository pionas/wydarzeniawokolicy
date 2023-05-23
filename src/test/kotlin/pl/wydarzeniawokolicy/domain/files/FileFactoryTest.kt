package pl.wydarzeniawokolicy.domain.files

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import pl.wydarzeniawokolicy.domain.shared.DateTimeUtils
import pl.wydarzeniawokolicy.domain.shared.FileUtils
import java.time.LocalDateTime


class FileFactoryTest {
    private val dateTimeUtils: DateTimeUtils = mock()
    private val fileUtils: FileUtils = mock()
    private val fileFactory: FileFactory = FileFactory(dateTimeUtils, fileUtils)

    @Test
    fun shouldCreateFile() {
        // given
        val localDateTime = LocalDateTime.of(2023, 5, 22, 11, 12, 0, 0)
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        whenever(fileUtils.saveFile(any(), any())).thenReturn("path/to/file.png")
        whenever(fileUtils.getHash(any())).thenReturn("hashmd5")
        // when
        val file = fileFactory.create(getFile())
        // then
        Assertions.assertThat(file)
            .hasFieldOrPropertyWithValue("hash", "hashmd5")
            .hasFieldOrPropertyWithValue("name", "hello.txt")
            .hasFieldOrPropertyWithValue("path", "path/to/file.png")
            .hasFieldOrPropertyWithValue("createdAt", localDateTime)
    }

    private fun getFile(): MultipartFile {
        return MockMultipartFile(
            "file",
            "hello.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "Hello, World!".toByteArray()
        )
    }
}