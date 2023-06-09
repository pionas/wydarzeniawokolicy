package pl.wydarzeniawokolicy.web.views

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import pl.wydarzeniawokolicy.domain.events.api.Event
import pl.wydarzeniawokolicy.web.MainLayout
import java.util.function.Consumer

@Route(value = "events", layout = MainLayout::class)
@AnonymousAllowed
@Component
@Scope("prototype")
class EventView : VerticalLayout() {

    private var grid: Grid<Event> = Grid(Event::class.java)
    private var filterText = TextField()

    init {
        addClassName("event-list-view")
        setSizeFull()
        configureGrid()
        add(getToolbar(), grid)
    }

    private fun configureGrid() {
        grid.addClassNames("events-grid")
        grid.setSizeFull()
        grid.setColumns("name", "description", "online", "createdAt")
//        grid.addColumn(ValueProvider<Event, Any> { event: Event ->
//            event.name
//        }).setHeader("Status")
//        grid.addColumn(ValueProvider<Event, Any> { event: Event ->
//            event.getCompany().getName()
//        }).setHeader("Company")
        grid.columns.forEach(Consumer { col: Grid.Column<Event?> ->
            col.setAutoWidth(
                true
            )
        })
    }

    private fun getToolbar(): HorizontalLayout {
        filterText.placeholder = "Filter by name..."
        filterText.isClearButtonVisible = true
        filterText.valueChangeMode = ValueChangeMode.LAZY
        val addContactButton = Button("Add event")
        val toolbar = HorizontalLayout(filterText, addContactButton)
        toolbar.addClassName("toolbar")
        return toolbar
    }
}