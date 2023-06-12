package pl.wydarzeniawokolicy.web.views

import com.github.mvysny.kaributesting.v10._get
import com.github.mvysny.kaributesting.v10.expectRows
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.textfield.TextField
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import pl.wydarzeniawokolicy.BasicIT
import pl.wydarzeniawokolicy.domain.categories.api.Category
import pl.wydarzeniawokolicy.web.category.CategoryView

class CategoryViewTest : BasicIT() {

    @Autowired
    lateinit var categoryView: CategoryView

    @Test
    @Sql(scripts = ["/db/categories.sql"], config = SqlConfig(encoding = "UTF-8"))
    fun showList() {
        val grid = categoryView._get<Grid<Category>>()
        grid.expectRows(3)
    }

    @Test
    @Sql(scripts = ["/db/categories.sql"], config = SqlConfig(encoding = "UTF-8"))
    fun filterCategoryList() {
        val filterCategoryTextField = categoryView._get<TextField> {
            placeholder = "Filter by name..."
        }
        filterCategoryTextField.value = "Category 1"

        val grid = categoryView._get<Grid<Category>>()
        grid.expectRows(1)
    }

    @Test
    @Sql(scripts = ["/db/categories.sql"], config = SqlConfig(encoding = "UTF-8"))
    fun showCategoryForm() {
        val categoryCreateButton = categoryView._get<Button>() {
            text = "Add category"
        }
        categoryCreateButton.click()
    }
}