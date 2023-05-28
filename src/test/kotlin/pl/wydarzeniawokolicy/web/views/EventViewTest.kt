package pl.wydarzeniawokolicy.web.views

import com.vaadin.testbench.unit.SpringUIUnitTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class EventViewTest : SpringUIUnitTest() {

    @Test
    fun shouldComponentCount() {
        val view: EventView = navigate(EventView::class.java)
        assertEquals(2, view.componentCount)
    }
}