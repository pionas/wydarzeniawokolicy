package pl.wydarzeniawokolicy.web.views

import com.github.mvysny.kaributesting.v10._get
import com.github.mvysny.kaributesting.v10.expectRows
import com.vaadin.flow.component.grid.Grid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import pl.wydarzeniawokolicy.BasicIT
import pl.wydarzeniawokolicy.domain.events.api.Event

class EventViewTest : BasicIT() {

    @Autowired
    lateinit var eventView: EventView

    @Test
    fun formShownWhenContactSelected() {
        val grid = eventView._get<Grid<Event>>()
        grid.expectRows(0)
    }
}