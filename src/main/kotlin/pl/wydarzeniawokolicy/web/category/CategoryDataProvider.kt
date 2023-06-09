package pl.wydarzeniawokolicy.web.category

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider
import com.vaadin.flow.data.provider.Query
import com.vaadin.flow.data.provider.SortDirection
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import pl.wydarzeniawokolicy.domain.categories.api.Category
import pl.wydarzeniawokolicy.domain.categories.api.CategoryFilter
import pl.wydarzeniawokolicy.domain.categories.api.CategoryService
import java.util.stream.Stream
import kotlin.jvm.optionals.getOrDefault

class CategoryDataProvider(private val service: CategoryService) :
    AbstractBackEndDataProvider<Category, CategoryFilter>() {
    override fun fetchFromBackEnd(query: Query<Category, CategoryFilter>): Stream<Category> {
        return service.findAll(filter(query))
            .stream()
    }

    override fun sizeInBackEnd(query: Query<Category, CategoryFilter>): Int =
        service.count(query.filter.getOrDefault(CategoryFilter()))

    private fun filter(query: Query<Category, CategoryFilter>): CategoryFilter {
        val filter = query.filter.getOrDefault(CategoryFilter())
        filter.pageable = PageRequest.of(query.page, query.pageSize, sorts(query))
        return filter
    }

    private fun sorts(query: Query<Category, CategoryFilter>): Sort {
        val sorts: List<Sort.Order> = query.sortOrders.map {
            when (it.direction) {
                SortDirection.ASCENDING -> Sort.Order.asc(it.sorted)
                else -> Sort.Order.desc(it.sorted)
            }
        }
        return Sort.by(sorts)
    }
}
