package pl.wydarzeniawokolicy.web.category

import com.vaadin.flow.component.Key
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.BeanValidationBinder
import com.vaadin.flow.data.binder.BinderValidationStatus
import pl.wydarzeniawokolicy.domain.categories.api.Category
import pl.wydarzeniawokolicy.shared.NewCategoryDto


class CategoryForm(private val category: Category?, categoryFormAction: CategoryFormAction) : FormLayout() {

    var name = TextField("Name")
    var slug = TextField("Slug")

    private val binder: BeanValidationBinder<NewCategoryDto> = BeanValidationBinder(NewCategoryDto::class.java)

    private var save = Button("Save") {
        val status: BinderValidationStatus<NewCategoryDto> = binder.validate()
        if (status.isOk) {
            val newCategoryDto = NewCategoryDto()
            binder.writeBeanIfValid(newCategoryDto)
            categoryFormAction.save(newCategoryDto.name!!, newCategoryDto.slug)
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
        if (category == null) {
            delete.isVisible = false
        }
        return HorizontalLayout(save, delete, close)
    }

    private fun bindToBean() {
        binder.forField(name)
            .bind("name")
        binder.forField(slug)
            .withNullRepresentation("")
            .bind("slug")
    }

}