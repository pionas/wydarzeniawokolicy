package pl.wydarzeniawokolicy.web.views

import com.github.mvysny.dynatest.DynaTest
import com.github.mvysny.kaributesting.v10._get
import com.github.mvysny.kaributesting.v10.expectRows
import com.github.mvysny.kaributools.navigateTo
import com.vaadin.flow.component.grid.Grid
import pl.wydarzeniawokolicy.domain.events.api.Event

class EventViewTest : DynaTest({

    usingApp()

    beforeEach {
        navigateTo<EventView>()
    }

    test("show event grid") {
        val grid = _get<Grid<Event>>()
        grid.expectRows(0)
    }
})