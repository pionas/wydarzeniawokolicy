package pl.wydarzeniawokolicy.web.views

import com.github.mvysny.kaributesting.v10._get
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.login.LoginForm
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import pl.wydarzeniawokolicy.BasicIT
import kotlin.test.assertEquals


class LoginViewTest : BasicIT() {

    @Autowired
    lateinit var loginView: LoginView

    @Test
    fun formShownWhenContactSelected() {
        val loginForm = loginView._get<LoginForm>()
        val header = loginView._get<H1>()
        assertEquals("Vaadin CRM", header.text)
        assertEquals("login", loginForm.action)
    }
}