package pl.wydarzeniawokolicy.domain.files

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import pl.wydarzeniawokolicy.domain.files.api.File
import pl.wydarzeniawokolicy.domain.files.api.FileNotFoundException
import pl.wydarzeniawokolicy.domain.files.api.FileService

@Service
class FileServiceImpl(val repository: FileRepository, val factory: FileFactory) : FileService {

    override fun findAll(): List<File> {
        return repository.findAll()
    }

    override fun create(file: MultipartFile): File {
        val fileCreated = factory.create(file)
        return repository.findByHash(fileCreated.hash) ?: repository.save(fileCreated)
    }

    override fun findByHash(hash: String): File {
        return repository.findByHash(hash) ?: throw FileNotFoundException(hash)
    }

    override fun deleteByHash(hash: String) {
        return repository.deleteByHash(hash)
    }
}