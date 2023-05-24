package pl.wydarzeniawokolicy.api.files

import org.mapstruct.Mapper
import pl.wydarzeniawokolicy.domain.files.api.File

@Mapper(componentModel = "spring")
interface FileMapper {

    fun mapToDto(createdUser: File): FileDto

}
