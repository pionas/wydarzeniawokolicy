package pl.wydarzeniawokolicy.web.category

import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.notification.NotificationVariant
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.renderer.ComponentRenderer
import com.vaadin.flow.data.selection.SelectionEvent
import com.vaadin.flow.data.value.ValueChangeMode
import com.vaadin.flow.router.HasDynamicTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.router.RouteParameters
import com.vaadin.flow.server.auth.AnonymousAllowed
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import pl.wydarzeniawokolicy.domain.categories.api.Category
import pl.wydarzeniawokolicy.domain.categories.api.CategoryService
import pl.wydarzeniawokolicy.domain.categories.api.NewCategory
import pl.wydarzeniawokolicy.web.AppProperties
import pl.wydarzeniawokolicy.web.MainLayout


@Route(value = "categories", layout = MainLayout::class)
@AnonymousAllowed
@Component
@Scope("prototype")
class CategoryView(private val service: CategoryService, private val appProperties: AppProperties) :
    VerticalLayout(), HasDynamicTitle {

    private var grid: Grid<Category> = Grid(Category::class.java)
    private var filterText = TextField()

    init {
        addClassName("category-list-view")
        setSizeFull()
        configureGrid()
        add(getToolbar(), grid)
    }

    override fun getPageTitle(): String = appProperties.category.title

    private fun configureGrid() {
        grid.addClassNames("categories-grid")
        grid.setSelectionMode(Grid.SelectionMode.MULTI)
        grid.setSizeFull()
        grid.setColumns("name", "createdAt", "updatedAt")
        grid.columns.forEach { it.setAutoWidth(true) }
        grid.addSelectionListener { selection: SelectionEvent<Grid<Category>, Category> ->
            System.out.printf(
                "### Number of selected people: %s%n",
                selection.allSelectedItems.size
            )
        }
        grid.addColumn(ComponentRenderer({ Button() }) { button: Button, category: Category ->
            button.addClickListener { _: ClickEvent<Button> ->
                showDialog(category)
            }
            button.text = "Edit"
        })
        grid.addColumn(ComponentRenderer({ Button() }) { button: Button, category: Category ->
            button.addClickListener { _: ClickEvent<Button> ->
                button.ui.ifPresent {
                    it.navigate(
                        CategoryDetailsView::class.java,
                        RouteParameters("slug", category.slug)
                    )
                }
            }
            button.text = "Show"
        })
        setItems()
    }

    private fun getToolbar(): HorizontalLayout {
        filterText.placeholder = "Filter by name..."
        filterText.isClearButtonVisible = true
        filterText.valueChangeMode = ValueChangeMode.LAZY
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
                    setItems()
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
                setItems()
                dialog.close()
            }

        })

        dialog.headerTitle = category?.let { "Edit category" } ?: "New category"
        dialog.add(categoryForm)
        dialog.open()
    }

    private fun setItems() {
        grid.setItems(service.findAll())
    }
}