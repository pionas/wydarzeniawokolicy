package pl.wydarzeniawokolicy.web.views

import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.login.LoginForm
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.BeforeEnterObserver
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Route("login")
@PageTitle("Login | Vaadin CRM")
@Component
@Scope("prototype")
class LoginView : VerticalLayout(), BeforeEnterObserver {

    private val login = LoginForm()

    init {
        addClassName("login-view")
        setSizeFull()
        alignItems = FlexComponent.Alignment.CENTER
        justifyContentMode = FlexComponent.JustifyContentMode.CENTER
        login.action = "login"
        add(H1("Vaadin CRM"), login)
    }

    override fun beforeEnter(beforeEnterEvent: BeforeEnterEvent) {
        if (beforeEnterEvent.location
                .queryParameters
                .parameters
                .containsKey("error")
        ) {
            login.isError = true
        }
    }
}