package pl.wydarzeniawokolicy.api.files

import lombok.AllArgsConstructor
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import pl.wydarzeniawokolicy.domain.files.api.FileService

@RestController
@RequestMapping("/api/v1/files")
@AllArgsConstructor
class FileRestController(
    private val service: FileService,
    private val mapper: FileMapper,
) {

    @PostMapping("")
    fun uploadFile(@RequestParam("file") file: MultipartFile): ResponseEntity<FileDto> {
        val createdUser = service.create(file)
        return ResponseEntity(mapper.mapToDto(createdUser), HttpStatus.CREATED)
    }
}