package pl.wydarzeniawokolicy.web.category

import com.vaadin.flow.component.Key
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.BeanValidationBinder
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.converter.StringToIntegerConverter
import com.vaadin.flow.function.SerializablePredicate
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import pl.wydarzeniawokolicy.domain.categories.api.Category


class CategoryForm(category: Category?, categoryFormAction: CategoryFormAction) : FormLayout() {

    @NotEmpty
    @Size(min = 3, max = 100)
    var name = TextField("Name")

    @Size(min = 3, max = 103)
    var slug = TextField("Slug")


//    var binder: Binder<Category> = Binder(Category::class.java)
    private val binder: BeanValidationBinder<Category> = BeanValidationBinder(Category::class.java)

    private var save = Button("Save") {
        if (binder.validate().isOk) {
            categoryFormAction.save(name.value, slug.value)
        }
    }

    private var delete = Button("Delete") {
        categoryFormAction.delete()
    }

    private var close = Button("Cancel") {
        categoryFormAction.close()
    }

    init {
        addClassName("category-form")
        val formLayout = FormLayout()
        formLayout.add(name, slug, createButtonsLayout())
        add(formLayout)
        category?.let {
            name.value = it.name
            slug.value = it.slug
        }
        bindToBean()
    }

    private fun createButtonsLayout(): HorizontalLayout {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR)
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY)
        save.addClickShortcut(Key.ENTER)
        close.addClickShortcut(Key.ESCAPE)
        return HorizontalLayout(save, delete, close)
    }

    private fun bindToBean() {
//        binder.bind(name, "name")
//        binder.bind(slug, "slug")
        binder.bindInstanceFields(this)
    }

}