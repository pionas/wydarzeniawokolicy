package pl.wydarzeniawokolicy.api.categories

import org.mapstruct.Mapper
import pl.wydarzeniawokolicy.domain.categories.api.Category
import pl.wydarzeniawokolicy.domain.categories.api.NewCategory

@Mapper(componentModel = "spring")
interface CategoryMapper {

    fun mapToDto(category: Category): CategoryDto
    fun mapToDto(categories: List<Category>): List<CategoryDto>
    fun mapToDomain(category: NewCategoryDto): NewCategory

}
