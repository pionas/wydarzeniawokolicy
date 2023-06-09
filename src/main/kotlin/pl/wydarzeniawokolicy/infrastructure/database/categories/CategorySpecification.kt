package pl.wydarzeniawokolicy.infrastructure.database.categories

import org.springframework.data.jpa.domain.Specification
import pl.wydarzeniawokolicy.domain.categories.api.Category
import java.util.*

class CategorySpecification {

    companion object {
        fun slugEquals(slug: String?): Specification<CategoryEntity> {
            return Specification { root, _, criteriaBuilder ->
                slug?.let {
                    criteriaBuilder.like(
                        criteriaBuilder.upper(root.get(Category.SLUG)),
                        "%" + it.uppercase(Locale.getDefault()) + "%"
                    )
                }
            }
        }

        fun nameEquals(name: String?): Specification<CategoryEntity> {
            return Specification { root, _, criteriaBuilder ->
                name?.let {
                    criteriaBuilder.like(
                        criteriaBuilder.upper(root.get(Category.NAME)),
                        "%" + it.uppercase(Locale.getDefault()) + "%"
                    )
                }
            }
        }
    }
}
