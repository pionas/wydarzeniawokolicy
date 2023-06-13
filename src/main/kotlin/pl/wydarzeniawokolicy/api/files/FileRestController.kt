package pl.wydarzeniawokolicy.api.files

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import pl.wydarzeniawokolicy.domain.files.api.FileService

@RestController
@RequestMapping("/api/v1/files")
class FileRestController(
    private val service: FileService,
    private val mapper: FileMapper,
) {

    @GetMapping
    fun getAll(): List<FileDto> = mapper.mapToDto(service.findAll())

    @PostMapping
    fun uploadFile(@Valid @RequestParam("file") file: MultipartFile): ResponseEntity<FileDto> {
        val createdUser = service.create(file)
        return ResponseEntity(mapper.mapToDto(createdUser), HttpStatus.CREATED)
    }

    @GetMapping("/{hash}")
    fun getById(@PathVariable("hash") hash: String): ResponseEntity<FileDto> {
        return ResponseEntity(mapper.mapToDto(service.findByHash(hash)), HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('FILE_MANAGEMENT','ADMIN','FILE_DELETE')")
    fun delete(@PathVariable("id") hash: String): ResponseEntity<Unit> {
        service.deleteByHash(hash)
        return ResponseEntity(HttpStatus.OK)
    }

}