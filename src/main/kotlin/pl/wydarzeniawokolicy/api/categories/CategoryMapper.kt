package pl.wydarzeniawokolicy.api.categories

import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy
import pl.wydarzeniawokolicy.domain.categories.api.Category
import pl.wydarzeniawokolicy.domain.categories.api.NewCategory
import pl.wydarzeniawokolicy.shared.CategoryDto
import pl.wydarzeniawokolicy.shared.NewCategoryDto

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface CategoryMapper {

    fun mapToDto(category: Category): CategoryDto
    fun mapToDto(categories: List<Category>): List<CategoryDto>
    fun mapToDomain(category: NewCategoryDto): NewCategory

}
