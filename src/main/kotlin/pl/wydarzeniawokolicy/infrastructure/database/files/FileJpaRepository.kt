package pl.wydarzeniawokolicy.infrastructure.database.files

import org.springframework.data.repository.CrudRepository

interface FileJpaRepository : CrudRepository<FileEntity, String>
