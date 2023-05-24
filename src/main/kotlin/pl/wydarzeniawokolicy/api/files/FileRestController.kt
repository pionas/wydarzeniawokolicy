package pl.wydarzeniawokolicy.api.files

import jakarta.validation.Valid
import lombok.AllArgsConstructor
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import pl.wydarzeniawokolicy.domain.files.api.FileException
import pl.wydarzeniawokolicy.domain.files.api.FileNotFoundException
import pl.wydarzeniawokolicy.domain.files.api.FileService

@RestController
@RequestMapping("/api/v1/files")
@AllArgsConstructor
class FileRestController(
    private val service: FileService,
    private val mapper: FileMapper,
) {

    @GetMapping("")
    fun getAll(): List<FileDto> = mapper.mapToDto(service.findAll())

    @PostMapping("")
    fun uploadFile(@Valid @RequestParam("file") file: MultipartFile): ResponseEntity<FileDto> {
        val createdUser = service.create(file)
        return ResponseEntity(mapper.mapToDto(createdUser), HttpStatus.CREATED)
    }

    @GetMapping("/{hash}")
    fun getById(@PathVariable("hash") hash: String): ResponseEntity<FileDto> {
        return ResponseEntity(mapper.mapToDto(service.findByHash(hash)), HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") hash: String): ResponseEntity<Void> {
        service.deleteByHash(hash)
        return ResponseEntity(HttpStatus.OK)
    }

    @ExceptionHandler(FileNotFoundException::class)
    fun exception(exception: FileNotFoundException): ResponseEntity<String> {
        return ResponseEntity(exception.message, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(FileException::class)
    fun exception(exception: FileException): ResponseEntity<String> {
        return ResponseEntity(exception.message, HttpStatus.BAD_REQUEST)
    }

}