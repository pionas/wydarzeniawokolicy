package pl.wydarzeniawokolicy.infrastructure.database.files

import org.springframework.stereotype.Repository
import pl.wydarzeniawokolicy.domain.files.FileRepository
import pl.wydarzeniawokolicy.domain.files.api.File
import java.util.*

@Repository
class FileRepositoryImpl(val fileJpaRepository: FileJpaRepository) : FileRepository {

    override fun findAll(): List<File> {
        return fileJpaRepository.findAll()
            .map { File(it) }
    }

    override fun save(file: File): File {
        val fileEntity = FileEntity(file.hash, file.name, file.path, file.createdAt, file.updatedAt, file.deletedAt)
        return File(fileJpaRepository.save(fileEntity))
    }

    override fun findByHash(hash: String): Optional<File> {
        return fileJpaRepository.findById(hash)
            .map { File(it) }
    }

    override fun deleteByHash(hash: String) {
        fileJpaRepository.deleteById(hash)
    }
}