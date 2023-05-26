package pl.wydarzeniawokolicy.domain.events.api

import pl.wydarzeniawokolicy.domain.shared.ModelException
import pl.wydarzeniawokolicy.domain.shared.NotFoundException

class EventException {
    companion object {
        fun eventNotFound(slug: String) = NotFoundException("Event by slug $slug not exist")
        fun slugExist(slug: String) = ModelException("Event by slug $slug exist")
    }
}