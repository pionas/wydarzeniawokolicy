package pl.wydarzeniawokolicy.domain.files.api

import pl.wydarzeniawokolicy.domain.shared.BasicModel
import pl.wydarzeniawokolicy.infrastructure.database.files.FileEntity
import java.io.InputStream
import java.time.LocalDateTime

class FileUpload(
    val name: String,
    val inputStream: InputStream
)

class File(
    val hash: String,
    val name: String,
    val path: String,
    createdAt: LocalDateTime,
    updatedAt: LocalDateTime?,
    deletedAt: LocalDateTime?
) : BasicModel(createdAt, updatedAt, deletedAt) {

    constructor(fileEntity: FileEntity) : this(
        hash = fileEntity.hash,
        name = fileEntity.name,
        path = fileEntity.path,
        createdAt = fileEntity.createdAt,
        updatedAt = fileEntity.updatedAt,
        deletedAt = fileEntity.deletedAt
    )
}