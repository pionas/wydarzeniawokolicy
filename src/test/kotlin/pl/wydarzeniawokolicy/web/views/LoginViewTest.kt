package pl.wydarzeniawokolicy.web.views

import com.github.mvysny.kaributesting.v10._get
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.login.LoginForm
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals


@SpringBootTest
class LoginViewTest {

    @Autowired
    lateinit var view: LoginView

    @Test
    fun formShownWhenContactSelected() {
        val loginForm = view._get<LoginForm>()
        val header = view._get<H1>()
        assertEquals("Vaadin CRM", header.text)
        assertEquals("login", loginForm.action)
    }
}