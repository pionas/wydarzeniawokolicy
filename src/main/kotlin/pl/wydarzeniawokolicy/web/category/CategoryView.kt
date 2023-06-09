package pl.wydarzeniawokolicy.web.category

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.notification.NotificationVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.data.value.ValueChangeMode
import com.vaadin.flow.function.ValueProvider
import com.vaadin.flow.router.HasDynamicTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.router.RouteParameters
import com.vaadin.flow.server.auth.AnonymousAllowed
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import pl.wydarzeniawokolicy.domain.categories.api.Category
import pl.wydarzeniawokolicy.domain.categories.api.CategoryFilter
import pl.wydarzeniawokolicy.domain.categories.api.CategoryService
import pl.wydarzeniawokolicy.domain.categories.api.NewCategory
import pl.wydarzeniawokolicy.web.AppProperties
import pl.wydarzeniawokolicy.web.MainLayout
import java.time.format.DateTimeFormatter


@Route(value = "categories", layout = MainLayout::class)
@AnonymousAllowed
@Component
@Scope("prototype")
class CategoryView(
    private val service: CategoryService,
    private val appProperties: AppProperties,
) : VerticalLayout(), HasDynamicTitle {

    private val categoryFilter: CategoryFilter = CategoryFilter()
    private val dataProvider: CategoryDataProvider = CategoryDataProvider(service)

    private val filterDataProvider: ConfigurableFilterDataProvider<Category, Void, CategoryFilter> = dataProvider
        .withConfigurableFilter()

    private var grid: Grid<Category> = Grid(Category::class.java, false)
    private var filterText = TextField()

    init {
        addClassName("category-list-view")
        setSizeFull()
        configureGrid()
        add(getToolbar(), grid)
        isPadding = false
    }

    override fun getPageTitle(): String = appProperties.category.title

    private fun configureGrid() {
        grid.addClassNames("categories-grid")
        grid.setSelectionMode(Grid.SelectionMode.MULTI)
        grid.pageSize = 20
        grid.setSizeFull()
        grid.setColumns(Category.NAME, Category.SLUG)
        grid.addColumn(ValueProvider<Category, Any> { category: Category ->
            category.createdAt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))
        }).setHeader(Category.CREATED_AT)
        grid.addColumn(ValueProvider<Category, Any> { category: Category ->
            category.updatedAt?.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))
        }).setHeader(Category.UPDATED_AT)

        grid.addColumn(ComponentRenderer({ Div() }) { div: Div, category: Category ->
            val buttonEdit = Button("Edit") {
                addClickListener {
                    showDialog(category)
                }
            }
            val buttonShow = Button("Show") {
                ui.ifPresent {
                    it.navigate(
                        CategoryDetailsView::class.java,
                        RouteParameters("slug", category.slug)
                    )
                }
            }
            div.add(buttonEdit, buttonShow)
        }).setHeader("Actions")
            .setKey("actions")

        grid.columns.forEach { it.setAutoWidth(true) }
        grid.setItems(filterDataProvider)
    }

    private fun getToolbar(): HorizontalLayout {
        filterText.placeholder = "Filter by name..."
        filterText.isClearButtonVisible = true
        filterText.valueChangeMode = ValueChangeMode.LAZY
        filterText.addValueChangeListener { e ->
            categoryFilter.searchPhrase = e.value
            updateList()
        }
        val newCategoryButton = Button("Add category") { showDialog(null) }
        val toolbar = HorizontalLayout(filterText, newCategoryButton)
        toolbar.addClassName("toolbar")
        return toolbar
    }

    private fun showDialog(category: Category?) {
        val dialog = Dialog()
        val categoryForm = CategoryForm(category, object : CategoryFormAction {
            override fun save(name: String, slug: String?) {
                try {
                    category?.let {
                        service.update(it.slug, NewCategory(name, slug))
                    } ?: run {
                        service.create(NewCategory(name, slug))
                    }

                    updateList()
                    dialog.close()
                } catch (exception: Exception) {
                    val notification = Notification.show(exception.message)
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR)
                }
            }

            override fun close() {
                dialog.close()
            }

            override fun delete() {
                category?.let { service.delete(it.slug) }
                updateList()
                dialog.close()
            }

        })

        dialog.headerTitle = category?.let { "Edit category" } ?: "New category"
        dialog.add(categoryForm)
        dialog.open()
    }


    private fun updateList() = filterDataProvider.setFilter(categoryFilter)
}