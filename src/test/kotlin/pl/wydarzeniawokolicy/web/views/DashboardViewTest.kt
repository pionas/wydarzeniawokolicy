package pl.wydarzeniawokolicy.web.views

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import pl.wydarzeniawokolicy.BasicIT
import kotlin.test.assertEquals

class DashboardViewTest : BasicIT() {

    @Autowired
    lateinit var dashboardView: DashboardView

    @Test
    fun showDashboardView() {
        assertEquals("Custom Title :D", dashboardView.pageTitle)
    }
}