package pl.wydarzeniawokolicy.api.files

import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy
import pl.wydarzeniawokolicy.domain.files.api.File

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface FileMapper {

    fun mapToDto(file: File): FileDto
    fun mapToDto(files: List<File>): List<FileDto>

}
