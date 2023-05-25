package pl.wydarzeniawokolicy.domain.categories.api

import pl.wydarzeniawokolicy.domain.shared.ModelException
import pl.wydarzeniawokolicy.domain.shared.NotFoundException

class CategoryException {
    companion object {
        fun slugNotFound(slug: String) = NotFoundException("Category by slug $slug not exist")
        fun slugExist(slug: String) = ModelException("Category by slug $slug exist")
    }
}