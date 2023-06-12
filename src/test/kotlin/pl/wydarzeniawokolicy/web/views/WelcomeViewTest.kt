package pl.wydarzeniawokolicy.web.views

import com.github.mvysny.kaributesting.v10._get
import com.vaadin.flow.component.Text
import com.vaadin.flow.component.details.Details
import com.vaadin.flow.component.virtuallist.VirtualList
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import pl.wydarzeniawokolicy.BasicIT
import pl.wydarzeniawokolicy.domain.events.api.Event
import kotlin.test.assertEquals


class WelcomeViewTest : BasicIT() {

    @Autowired
    lateinit var welcomeView: WelcomeView

    @Test
    @Sql(scripts = ["/db/events.sql"], config = SqlConfig(encoding = "UTF-8"))
    fun showWelcomeView() {
        val mapImplementsText = welcomeView._get<Text>()
        assertEquals("TODO: Implements map", mapImplementsText.text)
    }
}