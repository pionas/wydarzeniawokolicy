package pl.wydarzeniawokolicy.domain.files

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import pl.wydarzeniawokolicy.domain.files.api.File
import pl.wydarzeniawokolicy.domain.files.api.FileNotFoundException
import pl.wydarzeniawokolicy.domain.files.api.FileService
import java.time.LocalDateTime

internal class FileServiceTest {

    private val repository: FileRepository = mock()
    private val factory: FileFactory = mock()
    private val service: FileService = FileServiceImpl(repository, factory)


    @Test
    fun shouldReturnEmptyList() {
        // given
        whenever(repository.findAll()).thenReturn(emptyList())
        // when
        val files = service.findAll()
        // then
        Assertions.assertThat(files).hasSize(0)
        verify(repository, times(1)).findAll()
    }

    @Test
    fun shouldReturnListOfFile() {
        // given
        whenever(repository.findAll()).thenReturn(fileList())
        // when
        val files = service.findAll()
        // then
        Assertions.assertThat(files).hasSize(3)
        Assertions.assertThat(files[0])
            .hasFieldOrPropertyWithValue("hash", "hash1")
            .hasFieldOrPropertyWithValue("name", "name1")
            .hasFieldOrPropertyWithValue("path", "path1")
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 24, 16, 39, 0, 0))
        Assertions.assertThat(files[1])
            .hasFieldOrPropertyWithValue("hash", "hash2")
            .hasFieldOrPropertyWithValue("name", "name2")
            .hasFieldOrPropertyWithValue("path", "path2")
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 24, 16, 39, 10, 0))
        Assertions.assertThat(files[2])
            .hasFieldOrPropertyWithValue("hash", "hash3")
            .hasFieldOrPropertyWithValue("name", "name3")
            .hasFieldOrPropertyWithValue("path", "path3")
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 24, 16, 39, 20, 0))
        verify(repository, times(1)).findAll()
    }

    @Test
    fun shouldCreateFile() {
        // given
        val fileToCreate = getMultipartFile()
        val file = getFile("hash1", fileToCreate.name, "path1", LocalDateTime.of(2023, 5, 24, 16, 39, 0, 0))
        whenever(factory.create(fileToCreate)).thenReturn(file)
        whenever(repository.findByHash(file.hash)).thenReturn(null)
        // when
        service.create(fileToCreate)
        // then
        val argumentCaptor = argumentCaptor<File>()
        verify(repository, times(1)).save(argumentCaptor.capture())
        Assertions.assertThat(argumentCaptor.firstValue)
            .hasFieldOrPropertyWithValue("hash", "hash1")
            .hasFieldOrPropertyWithValue("name", fileToCreate.name)
            .hasFieldOrPropertyWithValue("path", "path1")
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 24, 16, 39, 0, 0))
        verify(repository, times(1)).save(any())
    }

    @Test
    fun shouldReturnExistFileByHash() {
        // given
        val fileToCreate = getMultipartFile()
        val file = getFile("hash1", fileToCreate.name, "path1", LocalDateTime.of(2023, 5, 24, 16, 39, 0, 0))
        whenever(factory.create(fileToCreate)).thenReturn(file)
        whenever(repository.findByHash(file.hash)).thenReturn(file)
        // when
        val fileExist = service.create(fileToCreate)
        // then
        Assertions.assertThat(fileExist)
            .hasFieldOrPropertyWithValue("hash", "hash1")
            .hasFieldOrPropertyWithValue("name", fileToCreate.name)
            .hasFieldOrPropertyWithValue("path", "path1")
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 24, 16, 39, 0, 0))
        verify(repository, times(0)).save(any())
    }

    @Test
    fun shouldReturnFileByHash() {
        // given
        val file = getFile("hash1", "name1", "path1", LocalDateTime.of(2023, 5, 24, 16, 39, 0, 0))
        whenever(repository.findByHash(file.hash)).thenReturn(file)
        // when
        val userDetails = service.findByHash(file.hash)
        // then
        Assertions.assertThat(userDetails)
            .hasFieldOrPropertyWithValue("hash", file.hash)
            .hasFieldOrPropertyWithValue("name", file.name)
            .hasFieldOrPropertyWithValue("path", file.path)
            .hasFieldOrPropertyWithValue("createdAt", file.createdAt)
            .hasFieldOrPropertyWithValue("updatedAt", file.updatedAt)
            .hasFieldOrPropertyWithValue("deletedAt", file.deletedAt)
        verify(repository, times(1)).findByHash(file.hash)
    }

    @Test
    fun shouldThrowExceptionWhenFileByHashNotExist() {
        // given
        val hash = "hash"
        whenever(repository.findByHash(hash)).thenReturn(null)
        // when
        val exception =
            Assertions.catchThrowableOfType({ service.findByHash(hash) }, FileNotFoundException::class.java)
        // then
        Assertions.assertThat(exception.message).isEqualTo("File by hash $hash not exists")
        verify(repository, times(1)).findByHash(hash)
    }

    @Test
    fun shouldDeleteById() {
        // given
        val fileHash = "hash"
        // when
        service.deleteByHash(fileHash)
        // then
        verify(repository, times(1)).deleteByHash(fileHash)
    }

    private fun fileList(): List<File> {
        return listOf(
            getFile("hash1", "name1", "path1", LocalDateTime.of(2023, 5, 24, 16, 39, 0, 0)),
            getFile("hash2", "name2", "path2", LocalDateTime.of(2023, 5, 24, 16, 39, 10, 0)),
            getFile("hash3", "name3", "path3", LocalDateTime.of(2023, 5, 24, 16, 39, 20, 0))
        )
    }

    private fun getFile(hash: String, name: String, path: String, createdAt: LocalDateTime) =
        File(hash, name, path, createdAt, null, null)


    private fun getMultipartFile(): MultipartFile {
        return MockMultipartFile(
            "file",
            "hello.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "Hello, World!".toByteArray()
        )
    }
}