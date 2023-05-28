package pl.wydarzeniawokolicy.domain.roles.api

import pl.wydarzeniawokolicy.domain.shared.ModelException
import pl.wydarzeniawokolicy.domain.shared.NotFoundException

class RoleException {
    companion object {
        fun slugNotFound(slug: String) = NotFoundException("Role by slug $slug not exist")
        fun slugExist(slug: String) = ModelException("Role by slug $slug exist")
    }
}