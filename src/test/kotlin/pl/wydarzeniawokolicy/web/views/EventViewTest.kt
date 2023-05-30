package pl.wydarzeniawokolicy.web.views

import com.github.mvysny.kaributesting.v10._get
import com.github.mvysny.kaributesting.v10.expectRows
import com.vaadin.flow.component.grid.Grid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pl.wydarzeniawokolicy.domain.events.api.Event

@SpringBootTest
class EventViewTest {

    @Autowired
    lateinit var view: EventView

    @Test
    fun formShownWhenContactSelected() {
        val grid = view._get<Grid<Event>>()
        grid.expectRows(0)
    }
}