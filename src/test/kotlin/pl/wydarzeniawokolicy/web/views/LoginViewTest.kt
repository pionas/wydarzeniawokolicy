package pl.wydarzeniawokolicy.web.views

import com.github.mvysny.dynatest.DynaTest
import com.github.mvysny.kaributesting.v10._get
import com.github.mvysny.kaributools.navigateTo
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.login.LoginForm
import kotlin.test.expect


class LoginViewTest : DynaTest({

    usingApp()

    beforeEach {
        navigateTo<LoginView>()
    }

    test("show login form") {
        val loginForm = _get<LoginForm>()
        val header = _get<H1>()
        expect("Vaadin CRM") { header.text }
        expect("login") { loginForm.action }
    }
})