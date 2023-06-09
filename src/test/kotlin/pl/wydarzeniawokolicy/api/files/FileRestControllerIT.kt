package pl.wydarzeniawokolicy.api.files

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.core.ParameterizedTypeReference
import org.springframework.core.io.ClassPathResource
import org.springframework.http.*
import org.springframework.test.context.jdbc.Sql
import org.springframework.util.FileSystemUtils
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import pl.wydarzeniawokolicy.BasicIT
import pl.wydarzeniawokolicy.infrastructure.database.files.FileEntity
import java.nio.file.Paths
import java.time.LocalDate
import java.time.LocalDateTime


internal class FileRestControllerIT : BasicIT() {

    val localDateTime: LocalDateTime = LocalDateTime.of(2023, 5, 24, 11, 12, 0, 0)
    val localDate: LocalDate = LocalDate.of(2023, 5, 24)

    @BeforeEach
    internal fun setUp() {
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        whenever(dateTimeUtils.getLocalDateNow()).thenReturn(localDate)
    }

    @AfterEach
    override fun tearDown() {
        FileSystemUtils.deleteRecursively(Paths.get("uploads"))
        super.tearDown()
    }

    @Test
    @Disabled
    fun shouldReturnUnauthorized() {
        // given

        // when
        val result = restApiTemplate.getForEntity("/files", List::class.java)

        // then
        assertNotNull(result)
        assertEquals(HttpStatus.UNAUTHORIZED, result?.statusCode)
    }

    @Test
    @Sql("/db/files.sql")
    fun shouldReturnFileList() {
        // given
        // when
        val result: ResponseEntity<List<FileDto>> = restApiTemplate.exchange(
            "/files",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<List<FileDto>>() {})
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.OK, result.statusCode)
        val files = result.body!!
        Assertions.assertThat(files).hasSize(4)
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
        Assertions.assertThat(files[3])
            .hasFieldOrPropertyWithValue("hash", "7eba8b1c1ebc38e45c658354eace01de")
            .hasFieldOrPropertyWithValue("name", "name4")
            .hasFieldOrPropertyWithValue("path", "path4")
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 24, 16, 39, 30, 0))
    }

    @Test
    fun shouldReturnInternalServerErrorWhenTryCreate() {
        // given
        // when
        val result =
            Assertions.catchThrowableOfType(
                { restApiTemplate.postForEntity("/files", LinkedMultiValueMap<String, Any>(), Any::class.java) },
                HttpServerErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result?.statusCode)
    }

    @Test
    @Sql("/db/files.sql")
    fun shouldCreate() {
        // given
        val request = getMultipartFile("files/forest-1000x1000.jpg")
        // when
        val file = restApiTemplate.postForEntity("/files", request, FileDto::class.java)
        // then
        assertNotNull(file)
        assertEquals(HttpStatus.CREATED, file?.statusCode)
        Assertions.assertThat(file.body)
            .hasFieldOrPropertyWithValue("hash", "d2a9332290d027cee71409735cedaae8")
            .hasFieldOrPropertyWithValue("name", "forest-1000x1000.jpg")
            .hasFieldOrPropertyWithValue("createdAt", localDateTime)
    }

    @Test
    @Sql("/db/files.sql")
    fun shouldReturnFileDetailsByHash() {
        // given
        // when
        val result = restApiTemplate.getForEntity("/files/hash1", FileDto::class.java)
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.OK, result?.statusCode)
        Assertions.assertThat(result.body!!)
            .hasFieldOrPropertyWithValue("hash", "hash1")
            .hasFieldOrPropertyWithValue("name", "name1")
            .hasFieldOrPropertyWithValue("path", "path1")
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 24, 16, 39, 0, 0))
    }

    @Test
    fun shouldReturnNotFoundWhenTryGetFileDetailsById() {
        // given
        // when
        val result =
            Assertions.catchThrowableOfType(
                { restApiTemplate.getForEntity("/files/invalidhash", Object::class.java) },
                HttpClientErrorException::class.java
            )
        // then
        assertNotNull(result)
        assertEquals(HttpStatus.NOT_FOUND, result?.statusCode)
    }

    @Test
    @Sql("/db/files.sql")
    fun shouldReturnExistFileByHash() {
        // given
        whenever(dateTimeUtils.getLocalDateTimeNow()).thenReturn(localDateTime)
        val fileToUpload = getMultipartFile("files/mac-1000x1000.jpg")
        // when
        val file = restApiTemplate.postForEntity("/files", fileToUpload, FileDto::class.java)
        // then
        assertNotNull(file)
        assertEquals(HttpStatus.CREATED, file?.statusCode)
        Assertions.assertThat(file.body)
            .hasFieldOrPropertyWithValue("hash", "7eba8b1c1ebc38e45c658354eace01de")
            .hasFieldOrPropertyWithValue("name", "name4")
            .hasFieldOrPropertyWithValue("createdAt", LocalDateTime.of(2023, 5, 24, 16, 39, 30, 0))
    }

    @Test
    @Sql("/db/files.sql")
    fun shouldDelete() {
        // given
        // when
        val result = restApiTemplate.exchange("/files/hash1", HttpMethod.DELETE, null, Any::class.java)
        // then
        assertEquals(HttpStatus.OK, result.statusCode)
        val fileEntity = dbUtils.em().find(FileEntity::class.java, "hash1")
        assertNull(fileEntity)
    }

    private fun getMultipartFile(fileName: String?): HttpEntity<LinkedMultiValueMap<String, Any>> {
        val map = LinkedMultiValueMap<String, Any>()
        fileName?.let {
            map.add("file", ClassPathResource(it))
        }

        val headers = HttpHeaders()
        headers.contentType = MediaType.MULTIPART_FORM_DATA
        return HttpEntity<LinkedMultiValueMap<String, Any>>(
            map, headers
        )
    }
}