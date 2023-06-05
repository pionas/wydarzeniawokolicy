package pl.wydarzeniawokolicy.web.category

import com.vaadin.flow.component.Text
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.BeforeEnterObserver
import com.vaadin.flow.router.HasDynamicTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import pl.wydarzeniawokolicy.domain.categories.api.Category
import pl.wydarzeniawokolicy.domain.categories.api.CategoryService
import pl.wydarzeniawokolicy.web.MainLayout

@Route("category/:slug", layout = MainLayout::class)
@AnonymousAllowed
@Component
@Scope("prototype")
class CategoryDetailsView(private val service: CategoryService) : VerticalLayout(), HasDynamicTitle,
    BeforeEnterObserver {

    private lateinit var category: Category

    override fun getPageTitle(): String {
        return category.name
    }

    override fun beforeEnter(event: BeforeEnterEvent) {
        val slug = event.routeParameters.get("slug")
            .orElseThrow()
        category = service.findBySlug(slug!!)
        main()
    }

    private fun main() {
        addClassName("category-detalis")
        setSizeFull()
        add(
            H1(category.name),
            Text(category.slug),
            Label("Created at"),
            Text(category.createdAt.toString()),
            Label("Updated at"),
            Text(category.updatedAt.toString()),
            Label("Deleted at"),
            Text(category.deletedAt.toString())
        )
    }

}
